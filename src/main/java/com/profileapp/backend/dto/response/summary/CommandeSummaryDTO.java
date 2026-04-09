package com.profileapp.backend.dto.response.summary;

import com.profileapp.backend.entity.CommandeStatus;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommandeSummaryDTO {
    private Long id;
    private LocalDateTime dateCommande;
    private BigDecimal montant;
    private CommandeStatus status;
    private LocalDate dateLivraisonPrevu;
    private int ligneCount;
    private String contactEmail;
}
