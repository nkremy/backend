package com.profileapp.backend.dto.request;

import com.profileapp.backend.entity.ContactStatus;


import jakarta.validation.constraints.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactRequestDTO {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Le format de l'email est invalide")
    @Size(max = 150, message = "L'email ne peut pas dépasser 150 caractères")
    private String email;

    @Size(max = 100, message = "Le nom ne peut pas dépasser 100 caractères")
    private String nom;

    @Size(max = 20, message = "Le téléphone ne peut pas dépasser 20 caractères")
    private String telephone;

    @NotNull(message = "La direction est obligatoire")
    private ContactStatus status;
}
