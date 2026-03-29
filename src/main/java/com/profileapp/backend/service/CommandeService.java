package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.CommandeRequestDTO;
import com.profileapp.backend.dto.response.detail.CommandeDetailDTO;
import com.profileapp.backend.dto.response.summary.CommandeSummaryDTO;
import com.profileapp.backend.entity.CommandeStatus;
import java.util.List;

public interface CommandeService {
    CommandeDetailDTO createCommande(CommandeRequestDTO requestDTO);
    List<CommandeSummaryDTO> getAllCommandes();
    List<CommandeSummaryDTO> getAllCommandesByStatus(CommandeStatus status);
    List<CommandeSummaryDTO> getCommandesByContactId(Long contactId);
    CommandeDetailDTO getCommandeById(Long id);
    CommandeDetailDTO updateCommande(Long id, CommandeRequestDTO requestDTO);
    void deleteCommande(Long id);
}
