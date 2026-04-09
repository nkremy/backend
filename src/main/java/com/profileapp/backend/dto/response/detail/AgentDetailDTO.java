package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.AnalyseSummaryDTO;
import com.profileapp.backend.dto.response.summary.MessageSummaryDTO;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentDetailDTO {
    private Long id;
    private String model;
    private Boolean actif;
    private LocalDateTime dateActivation;
    private int messageCount;
    private int analyseCount;
    private List<MessageSummaryDTO> messages;
    private List<AnalyseSummaryDTO> analyses;
}
