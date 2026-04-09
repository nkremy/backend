package com.profileapp.backend.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SousServiceImageRequestDTO {

    @NotBlank(message = "L'URL de l'image est obligatoire")
    @Size(max = 500, message = "L'URL ne peut pas dépasser 500 caractères")
    private String imageUrl;

    private Integer ordre;
}
