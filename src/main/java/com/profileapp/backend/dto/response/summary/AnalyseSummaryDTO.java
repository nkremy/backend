package com.profileapp.backend.dto.response.summary;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class AnalyseSummaryDTO {
    private Long id;
    private String description;
    private LocalDateTime dateAnalyse;
    private Long messageId;
    private String agentModel;
}
