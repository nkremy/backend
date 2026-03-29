package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.AgentSummaryDTO;
import com.profileapp.backend.dto.response.summary.AnalyseSummaryDTO;
import com.profileapp.backend.entity.Direction;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MessageDetailDTO {
    private Long id;
    private LocalDateTime dateHeure;
    private Direction direction;
    private String sujetEmail;
    private String resumeIa;
    private String messageComplet;
    private String messageIdGmail;
    private String threadIdGmail;
    private Long echangeId;
    private AgentSummaryDTO agent;
    private AnalyseSummaryDTO analyse;
}
