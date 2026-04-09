package com.profileapp.backend.dto.response.summary;

import com.profileapp.backend.entity.Direction;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageSummaryDTO {
    private Long id;
    private LocalDateTime dateHeure;
    private Direction direction;
    private String sujetEmail;
    private String resumeIa;
    private String threadIdGmail;
    private boolean traitePaAgent;

    /* Infos du contact lié via Echange — pour le chip "Voir contact" */
    private Long contactId;
    private String contactEmail;
    private String contactNom;
}
