package com.profileapp.backend.dto.response.summary;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AgentSummaryDTO {
    private Long id;
    private String model;
    private Boolean actif;
    private LocalDateTime dateActivation;
}
