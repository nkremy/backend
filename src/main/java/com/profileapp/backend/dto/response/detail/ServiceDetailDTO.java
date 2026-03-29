package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.LigneCommandeSummaryDTO;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceDetailDTO {
    private Long id;
    private String nom;
    private BigDecimal prixBase;
    private String description;
    private int utilisationCount;
    private List<LigneCommandeSummaryDTO> ligneCommandes;
}
