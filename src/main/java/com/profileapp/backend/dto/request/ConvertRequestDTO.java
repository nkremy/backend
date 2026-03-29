package com.profileapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConvertRequestDTO {

    @NotBlank(message = "Le motif de conversion est obligatoire")
    private String motif;
}
