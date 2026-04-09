package com.profileapp.backend.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LigneCommandeRequestDTO {

    @NotNull(message = "L'identifiant du service est obligatoire")
    private Long serviceId;

    @NotNull(message = "L'identifiant de la commande est obligatoire")
    private Long commandeId;

    // Optionnel — référence au sous-service précis commandé
    // null = commande liée au service global (rétrocompatibilité)
    private Long sousServiceId;

    @NotNull(message = "Le prix unitaire est obligatoire")
    @Positive(message = "Le prix unitaire doit être positif")
    private BigDecimal prixUnitaire;

    private String detailsSpecifiques;
    private String dureeEstimation;
}
