package com.profileapp.backend.dto.response.summary;

import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ConversionLogSummaryDTO {
    private Long id;
    private LocalDateTime dateConversion;
    private String motif;
    private String contactEmail;
}
