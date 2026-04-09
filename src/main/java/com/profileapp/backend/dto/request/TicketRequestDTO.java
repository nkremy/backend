package com.profileapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketRequestDTO {

    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 200, message = "Le titre ne peut pas dépasser 200 caractères")
    private String titre;

    @NotBlank(message = "La description est obligatoire")
    private String description;

    @NotNull(message = "Le type est obligatoire (INFO ou ALERTE)")
    private String type;

    @NotNull(message = "La priorité est obligatoire (BASSE, MOYENNE, HAUTE, CRITIQUE)")
    private String priorite;

    @Size(max = 100)
    private String agentModel;
}
