package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.SousServiceImageSummaryDTO;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

/**
 * DTO public pour le catalogue — sans informations sensibles (utilisationCount, etc.)
 * Utilisé par l'endpoint public /api/v1/catalogue accessible sans authentification.
 */
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SousServiceCatalogueDTO {
    private Long id;
    private String nom;
    private String description;
    private BigDecimal prixBase;
    private Integer ordre;
    private List<SousServiceImageSummaryDTO> images;
}
