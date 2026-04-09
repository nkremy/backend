package com.profileapp.backend.dto.response.detail;

import com.profileapp.backend.dto.response.summary.*;
import com.profileapp.backend.entity.ContactStatus;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class ContactDetailDTO {
    private Long id;
    private String email;
    private String nom;
    private String telephone;
    private ContactStatus status;
    private Boolean actif;
    private LocalDateTime dateCreation;
    private EchangeSummaryDTO echange;
    private List<CommandeSummaryDTO> commandes;
    private ConversionLogSummaryDTO conversionLog;
}
