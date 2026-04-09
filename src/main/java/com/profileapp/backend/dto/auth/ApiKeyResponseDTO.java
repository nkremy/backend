package com.profileapp.backend.dto.auth;

import lombok.*;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ApiKeyResponseDTO {

    private Long id;
    private String nom;

    // La valeur de la clé — affichée UNE SEULE FOIS à la création
    // Après ça, elle n'est plus jamais retournée (l'admin ne peut que la révoquer)
    private String cleApi;

    private boolean actif;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;
}
