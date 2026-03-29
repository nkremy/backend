package com.profileapp.backend.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CommandeRequestDTO {

    @NotNull(message = "L'identifiant du contact est obligatoire")
    private Long contactId;

    @Positive(message = "Le montant doit être positif")
    private BigDecimal montant;

    private String descriptionBesoin;
    private String ficheDevisUrl;

    @NotNull(message = "la date de livraison prevu est aubligatoire")
    @Future(message = "la date de livraison prevu dois etre dans le future")
    private LocalDate dateLivraisonPrevu;

    // Lignes de commande à créer avec la commande
    @Valid
    private List<LigneCommandeRequestDTO> lignes;
}
