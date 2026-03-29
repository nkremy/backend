package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.AgentSummaryDTO;
import com.profileapp.backend.dto.response.summary.MessageSummaryDTO;
import lombok.*;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AnalyseDetailDTO {
    private Long id;
    private String description;
    private LocalDateTime dateAnalyse;
    private MessageSummaryDTO message;
    private AgentSummaryDTO agent;
}
