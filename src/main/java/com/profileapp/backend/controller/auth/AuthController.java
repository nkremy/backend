package com.profileapp.backend.controller.auth;

import com.profileapp.backend.dto.auth.LoginRequestDTO;
import com.profileapp.backend.dto.auth.LoginResponseDTO;
import com.profileapp.backend.dto.auth.RegisterRequestDTO;
import com.profileapp.backend.entity.auth.Admin;
import com.profileapp.backend.entity.auth.TokenBlacklist;
import com.profileapp.backend.exception.DuplicateResourceException;
import com.profileapp.backend.repository.auth.AdminRepository;
import com.profileapp.backend.repository.auth.TokenBlacklistRepository;
import com.profileapp.backend.security.JwtUtil;
import com.profileapp.backend.util.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AdminRepository adminRepository;
    private final TokenBlacklistRepository blacklistRepository;
    private final JwtUtil jwtUtil;
    private final PasswordEncoder passwordEncoder;

    // ════════════════════════════════════════════════════════════════
    // POST /api/v1/auth/register
    //
    // Crée le premier (et unique) admin du système.
    // En production : désactiver cet endpoint ou le protéger
    // une fois l'admin créé.
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/register")
    public ResponseEntity<ApiResponse<Void>> register(
            @Valid @RequestBody RegisterRequestDTO requestDTO) {

        if (adminRepository.existsByEmail(requestDTO.getEmail())) {
            throw new DuplicateResourceException(
                    "Un admin avec l'email " + requestDTO.getEmail() + " existe déjà");
        }

        Admin admin = Admin.builder()
                .email(requestDTO.getEmail())
                // encode() applique BCrypt — le résultat est différent à chaque appel
                // (le sel est intégré dans le hash), mais matches() retrouve toujours true
                .motDePasse(passwordEncoder.encode(requestDTO.getMotDePasse()))
                .build();

        adminRepository.save(admin);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Admin créé avec succès"));
    }

    // ════════════════════════════════════════════════════════════════
    // POST /api/v1/auth/login
    //
    // Vérifie les credentials et retourne un JWT.
    // React stocke ce token et l'envoie dans chaque requête suivante.
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponseDTO>> login(
            @Valid @RequestBody LoginRequestDTO requestDTO) {
        System.out.println("************** Login  ****************");

        // Chercher l'admin par email
        Admin admin = adminRepository.findByEmail(requestDTO.getEmail())
                .orElseThrow(() -> new RuntimeException("Identifiants invalides"));

        // Vérifier le mot de passe avec BCrypt
        // matches(brut, haché) — on ne peut pas comparer directement
        if (!passwordEncoder.matches(requestDTO.getMotDePasse(), admin.getMotDePasse())) {
            throw new RuntimeException("Identifiants invalides");
            // Même message que "email introuvable" — l'attaquant ne sait pas ce qui est
            // incorrect
        }

        String token = jwtUtil.generateToken(admin.getEmail());

        return ResponseEntity.ok(
                ApiResponse.success("Connexion réussie",
                        LoginResponseDTO.builder()
                                .token(token)
                                .email(admin.getEmail())
                                .expiresIn(86400000L)
                                .build()));
    }

    // ════════════════════════════════════════════════════════════════
    // POST /api/v1/auth/logout
    //
    // Blackliste le token courant — il sera refusé par JwtFilter
    // dès la prochaine requête, même s'il est encore valide.
    //
    // On lit le header Authorization directement depuis la requête
    // car cet endpoint est permis pour tous (/auth/**).
    // JwtFilter a déjà tourné avant ce controller mais le contexte
    // peut être vide si le token était absent ou invalide —
    // on gère les deux cas proprement.
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(HttpServletRequest request) {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        // Pas de token → déjà déconnecté → 200 quand même
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            return ResponseEntity.ok(ApiResponse.success("Déconnexion effectuée"));
        }

        String token = authHeader.substring(7);

        // On ne blackliste que les tokens valides
        // Un token expiré est déjà inutilisable — inutile de le stocker
        if (jwtUtil.validateToken(token)) {
            String email = jwtUtil.extractEmail(token);

            blacklistRepository.save(
                    TokenBlacklist.builder()
                            .token(token)
                            .email(email)
                            .expiresAt(jwtUtil.getExpirationFromToken(token))
                            .build());
        }

        return ResponseEntity.ok(ApiResponse.success("Déconnexion effectuée"));
    }
}
