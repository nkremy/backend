package com.profileapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnalyseRequestDTO {

    @NotNull(message = "L'identifiant du message est obligatoire")
    private Long messageId;

    @NotNull(message = "L'identifiant de l'agent est obligatoire")
    private Long agentId;

    @NotBlank(message = "La description est obligatoire")
    private String description;
}
