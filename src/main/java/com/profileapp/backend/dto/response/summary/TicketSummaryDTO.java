package com.profileapp.backend.dto.response.summary;

import com.profileapp.backend.entity.TicketPriorite;
import com.profileapp.backend.entity.TicketType;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketSummaryDTO {
    private Long id;
    private String titre;
    private String description;
    private TicketType type;
    private TicketPriorite priorite;
    private Boolean lu;
    private LocalDateTime dateCreation;
    private String agentModel;
}
