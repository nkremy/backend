package com.profileapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceRequestDTO {

    @NotBlank(message = "Le nom du service est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String nom;

    @Positive(message = "Le prix de base doit être positif")
    private BigDecimal prixBase;

    private String description;

    // Chemin ou URL vers l'image de couverture du pôle
    @Size(max = 500, message = "L'URL de l'image ne peut pas dépasser 500 caractères")
    private String imageUrl;

    private Integer ordre;
}
