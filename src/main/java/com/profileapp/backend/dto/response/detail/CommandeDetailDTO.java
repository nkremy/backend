package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.*;
import com.profileapp.backend.entity.CommandeStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeDetailDTO {
    private Long id;
    private LocalDateTime dateCommande;
    private BigDecimal montant;
    private CommandeStatus status;
    private String descriptionBesoin;
    private String ficheDevisUrl;
    private LocalDate dateLivraisonPrevu;
    private ContactSummaryDTO contact;
    private List<LigneCommandeSummaryDTO> ligneCommandes;
}
