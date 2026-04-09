package com.profileapp.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class CommandeRequestDTO {

    @NotNull(message = "L'identifiant du contact est obligatoire")
    private Long contactId;

    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String descriptionBesoin;
    private String ficheDevisUrl;

    /* Optionnel — peut être null si date non encore définie */
    private LocalDate dateLivraisonPrevu;

    /* Optionnel — statut de la commande, défaut DEVIS si absent */
    private String status;

    @Valid
    private List<LigneCommandeRequestDTO> lignes;
}
