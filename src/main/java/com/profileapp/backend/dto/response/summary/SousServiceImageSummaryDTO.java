package com.profileapp.backend.dto.response.summary;

import lombok.*;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class SousServiceImageSummaryDTO {
    private Long id;
    private String imageUrl;
    private Integer ordre;
}
