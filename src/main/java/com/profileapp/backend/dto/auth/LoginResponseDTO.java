package com.profileapp.backend.dto.auth;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LoginResponseDTO {

    // Le token JWT — React le stocke et l'envoie dans chaque requête suivante
    // Authorization: Bearer <token>
    private String token;

    // Email de l'admin connecté — pour afficher "Bonjour Admin" dans l'interface
    private String email;

    // Durée de vie en ms — React peut calculer l'heure d'expiration
    // et déconnecter automatiquement l'utilisateur quand elle arrive
    private long expiresIn;
}
