package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.LigneCommandeSummaryDTO;
import com.profileapp.backend.dto.response.summary.SousServiceSummaryDTO;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ServiceDetailDTO {
    private Long id;
    private String nom;
    private BigDecimal prixBase;
    private String description;
    private String imageUrl;
    private Integer ordre;
    private int utilisationCount;
    private List<SousServiceSummaryDTO> sousServices;
    private List<LigneCommandeSummaryDTO> ligneCommandes;
}
