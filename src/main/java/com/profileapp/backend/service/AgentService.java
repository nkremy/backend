package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.AgentRequestDTO;
import com.profileapp.backend.dto.response.detail.AgentDetailDTO;
import com.profileapp.backend.dto.response.summary.AgentSummaryDTO;
import java.util.List;

public interface AgentService {
    AgentDetailDTO createAgent(AgentRequestDTO requestDTO);
    List<AgentSummaryDTO> getAllAgents();
    AgentDetailDTO getAgentById(Long id);
    AgentDetailDTO updateAgent(Long id, AgentRequestDTO requestDTO);
    void deleteAgent(Long id);
}
