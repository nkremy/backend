package com.profileapp.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKeyRequestDTO {

    @NotBlank(message = "Le nom de la clé est obligatoire")
    private String nom;

    // null = pas d'expiration
    private LocalDateTime expiresAt;
}
