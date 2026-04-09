package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.SousServiceImageSummaryDTO;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SousServiceDetailDTO {
    private Long id;
    private Long serviceId;
    private String serviceNom;
    private String nom;
    private String description;
    private BigDecimal prixBase;
    private Integer ordre;
    private Boolean actif;
    private List<SousServiceImageSummaryDTO> images;
    private int utilisationCount;
}
