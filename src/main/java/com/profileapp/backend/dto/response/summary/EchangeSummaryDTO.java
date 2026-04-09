package com.profileapp.backend.dto.response.summary;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EchangeSummaryDTO {
    private Long id;
    private int messageCount;
    private List<MessageSummaryDTO> messages;
}
