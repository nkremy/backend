package com.profileapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgentRequestDTO {

    @NotBlank(message = "Le modèle est obligatoire")
    @Size(max = 100, message = "Le modèle ne peut pas dépasser 100 caractères")
    private String model;

    private Boolean actif;
}
