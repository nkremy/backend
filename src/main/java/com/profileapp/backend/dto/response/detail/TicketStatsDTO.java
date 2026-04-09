package com.profileapp.backend.dto.response.detail;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class TicketStatsDTO {
    private long total;
    private long nonLus;
    private long alertesNonLues;
    private long infosNonLues;
}
