package com.profileapp.backend.security;

import com.profileapp.backend.repository.auth.AdminRepository;
import com.profileapp.backend.repository.auth.TokenBlacklistRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * JwtFilter — intercepte chaque requête HTTP exactement une fois.
 *
 * OncePerRequestFilter garantit une seule exécution par requête,
 * même si Spring appelle la chaîne de filtres plusieurs fois en interne.
 *
 * Ordre des vérifications (optimisé pour éviter les appels base inutiles) :
 *   1. Header présent et format "Bearer ..." → sinon passe sans auth
 *   2. Signature + expiration valides (en mémoire, rapide) → sinon 401
 *   3. Token blacklisté (requête base) → sinon 401
 *   4. Admin existe encore en base → sinon 401
 *   5. Remplir le SecurityContext → la requête continue
 */
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final AdminRepository adminRepository;
    private final TokenBlacklistRepository blacklistRepository;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // ── Vérification 1 : header Authorization présent ────────────
        // HttpHeaders.AUTHORIZATION = "Authorization" (constante Spring)
        // Utiliser la constante évite les fautes de frappe sur le nom du header
        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            // Pas de token → on passe sans authentifier personne
            // SecurityConfig refusera l'accès aux endpoints protégés
            filterChain.doFilter(request, response);
            return;
        }

        // ── Vérification 2 : extraire le token ───────────────────────
        // "Bearer " = 7 caractères → substring(7) coupe ce préfixe
        String token = authHeader.substring(7);

        // ── Vérification 3 : signature et expiration (en mémoire) ────
        // On vérifie la cryptographie AVANT la base — plus rapide
        // Un token falsifié ou expiré ne doit jamais toucher la base
        if (!jwtUtil.validateToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token invalide ou expiré");
            return;
        }

        // ── Vérification 4 : token blacklisté (requête base) ─────────
        // On interroge la base seulement si le token est cryptographiquement valide
        if (blacklistRepository.existsByToken(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Token révoqué — reconnectez-vous");
            return;
        }

        // ── Vérification 5 : admin toujours en base ──────────────────
        // Le token peut être valide mais l'admin supprimé depuis son émission
        String email = jwtUtil.extractEmail(token);

        if (adminRepository.findByEmail(email).isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Admin introuvable");
            return;
        }

        // ── Authentification : remplir le SecurityContext ─────────────
        // UserDetails = interface Spring Security représentant l'utilisateur authentifié
        UserDetails userDetails = User
                .withUsername(email)
                .password("")
                .authorities(List.of())
                .build();

        // UsernamePasswordAuthenticationToken à 3 paramètres = "authentifié"
        // (à 2 paramètres = "en cours d'authentification" — nuance importante)
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null,
                        userDetails.getAuthorities()
                );

        // SecurityContextHolder = registre global Spring pour la requête courante
        // Après cette ligne, @AuthenticationPrincipal et SecurityContextHolder
        // retournent cet utilisateur dans toute la chaîne d'exécution
        SecurityContextHolder.getContext().setAuthentication(authToken);

        // Passer au filtre suivant (ou au controller si c'est le dernier filtre)
        filterChain.doFilter(request, response);
    }
}
