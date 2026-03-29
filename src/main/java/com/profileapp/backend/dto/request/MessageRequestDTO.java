package com.profileapp.backend.dto.request;

import com.profileapp.backend.entity.Direction;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageRequestDTO {

    @NotNull(message = "L'identifiant de l'échange est obligatoire")
    private Long echangeId;

    @NotNull(message = "La direction est obligatoire")
    private Direction direction;

    @NotNull(message = "La date et heure sont obligatoires")
    private LocalDateTime dateHeure;

    @Size(max = 255, message = "Le sujet ne peut pas dépasser 255 caractères")
    private String sujetEmail;

    @Size(max = 200, message = "le resume ne dois pas depasser 200 caracteres")
    private String resumeIa;

    @NotBlank(message = "le message d'origine est obligatoire")
    private String messageComplet;

    @Size(max = 255)
    private String messageIdGmail;

    @Size(max = 255)
    private String threadIdGmail;

    // null si message humain
    private Long agentId;
}
