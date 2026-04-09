package com.profileapp.backend.dto.response.summary;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SousServiceSummaryDTO {
    private Long id;
    private Long serviceId;
    private String serviceNom;
    private String nom;
    private String description;
    private BigDecimal prixBase;
    private Integer ordre;
    private Boolean actif;
    private List<SousServiceImageSummaryDTO> images;
}
