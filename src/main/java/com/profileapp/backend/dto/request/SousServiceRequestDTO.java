package com.profileapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SousServiceRequestDTO {

    @NotNull(message = "L'identifiant du service parent est obligatoire")
    private Long serviceId;

    @NotBlank(message = "Le nom du sous-service est obligatoire")
    @Size(max = 150, message = "Le nom ne peut pas dépasser 150 caractères")
    private String nom;

    private String description;

    @Positive(message = "Le prix de base doit être positif")
    private BigDecimal prixBase;

    private Integer ordre;

    private Boolean actif;

    // Liste des URLs d'images à associer au sous-service
    // Chaque URL est le chemin vers une image locale ou distante
    private List<String> imageUrls;
}
