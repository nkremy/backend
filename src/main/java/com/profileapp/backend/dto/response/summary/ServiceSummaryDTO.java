package com.profileapp.backend.dto.response.summary;

import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ServiceSummaryDTO {
    private Long id;
    private String nom;
    private BigDecimal prixBase;
    private String description;
}
