package com.profileapp.backend.security;

import com.profileapp.backend.entity.auth.ApiKey;
import com.profileapp.backend.repository.auth.ApiKeyRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * ApiKeyFilter — valide les clés API utilisées par les agents IA.
 *
 * L'agent envoie ses requêtes avec le header :
 *   X-API-Key: gno-agent-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
 *
 * Ce filtre s'exécute EN PARALLÈLE du JwtFilter — pas l'un après l'autre.
 * Si JwtFilter a déjà authentifié (token JWT valide), ce filtre passe sans rien faire.
 * Si le contexte est vide et qu'il y a un header X-API-Key, ce filtre prend le relais.
 */
@Component
@RequiredArgsConstructor
public class ApiKeyFilter extends OncePerRequestFilter {

    private final ApiKeyRepository apiKeyRepository;

    // Nom du header utilisé par les agents — convention établie
    private static final String API_KEY_HEADER = "X-API-Key";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        // Si quelqu'un est déjà authentifié (via JWT), on ne fait rien
        if (SecurityContextHolder.getContext().getAuthentication() != null) {
            filterChain.doFilter(request, response);
            return;
        }

        String cleApi = request.getHeader(API_KEY_HEADER);

        // Pas de clé API → on passe (JwtFilter ou SecurityConfig géreront le refus)
        if (cleApi == null || cleApi.isBlank()) {
            filterChain.doFilter(request, response);
            return;
        }

        // Chercher la clé en base
        Optional<ApiKey> apiKeyOpt = apiKeyRepository.findByCleApi(cleApi);

        if (apiKeyOpt.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Clé API invalide");
            return;
        }

        ApiKey apiKey = apiKeyOpt.get();

        // Vérifier que la clé est active
        if (!apiKey.isActif()) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Clé API révoquée");
            return;
        }

        // Vérifier l'expiration si une date est définie
        if (apiKey.getExpiresAt() != null && apiKey.getExpiresAt().isBefore(LocalDateTime.now())) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Clé API expirée");
            return;
        }

        // Clé valide → authentifier l'agent dans le SecurityContext
        // On utilise le nom de la clé comme "username" pour les logs
        UserDetails userDetails = User
                .withUsername("agent:" + apiKey.getNom())
                .password("")
                .authorities(List.of())
                .build();

        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);
        filterChain.doFilter(request, response);
    }
}
