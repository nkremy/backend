package com.profileapp.backend.dto.response.detail;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO public pour le catalogue — vue complète service + sous-services
 * Utilisé par l'endpoint public /api/v1/catalogue accessible sans authentification.
 * Ne contient pas les informations de commandes ou d'utilisation.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceCatalogueDTO {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prixBase;
    private String imageUrl;
    private Integer ordre;
    private List<SousServiceCatalogueDTO> sousServices;
}
