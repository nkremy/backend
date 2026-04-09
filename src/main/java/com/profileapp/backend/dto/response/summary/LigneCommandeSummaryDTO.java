package com.profileapp.backend.dto.response.summary;

import lombok.*;
import java.math.BigDecimal;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class LigneCommandeSummaryDTO {
    private Long id;
    private BigDecimal prixUnitaire;
    private String detailsSpecifiques;
    private String dureeEstimation;
    private ServiceSummaryDTO service;
    // Référence optionnelle au sous-service précis commandé
    private SousServiceSummaryDTO sousService;
}
