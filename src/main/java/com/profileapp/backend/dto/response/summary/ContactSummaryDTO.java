package com.profileapp.backend.dto.response.summary;

import com.profileapp.backend.entity.ContactStatus;
import lombok.*;
import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactSummaryDTO {
    private Long id;
    private String email;
    private String nom;
    private String telephone;
    private ContactStatus status;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private int messageCount;
    private int commandeCount;
    private boolean aConversionLog;
}
