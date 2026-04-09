package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.AgentRequestDTO;
import com.profileapp.backend.dto.response.detail.AgentDetailDTO;
import com.profileapp.backend.dto.response.summary.AgentSummaryDTO;
import com.profileapp.backend.dto.response.summary.AnalyseSummaryDTO;
import com.profileapp.backend.dto.response.summary.MessageSummaryDTO;
import com.profileapp.backend.entity.Agent;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.AgentRepository;
import com.profileapp.backend.service.AgentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public AgentDetailDTO createAgent(AgentRequestDTO requestDTO) {
        Agent agent = Agent.builder()
                .model(requestDTO.getModel())
                .actif(requestDTO.getActif() != null ? requestDTO.getActif() : true)
                .build();
        Agent saved = agentRepository.save(agent);
        return getAgentById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AgentSummaryDTO> getAllAgents() {
        return agentRepository.findAll()
                .stream()
                .map(a -> AgentSummaryDTO.builder()
                        .id(a.getId())
                        .model(a.getModel())
                        .actif(a.getActif())
                        .dateActivation(a.getDateActivation())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AgentDetailDTO getAgentById(Long id) {
        Agent agent = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Agent non trouvé avec l'id : " + id
                ));
        return mapToDetailDTO(agent);
    }

    @Override
    @Transactional
    public AgentDetailDTO updateAgent(Long id, AgentRequestDTO requestDTO) {
        Agent existing = agentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Agent non trouvé avec l'id : " + id
                ));
        existing.setModel(requestDTO.getModel());
        if (requestDTO.getActif() != null) {
            existing.setActif(requestDTO.getActif());
        }
        agentRepository.save(existing);
        return getAgentById(id);
    }

    @Override
    @Transactional
    public void deleteAgent(Long id) {
        if (!agentRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Agent non trouvé avec l'id : " + id
            );
        }
        agentRepository.deleteById(id);
    }

    private AgentDetailDTO mapToDetailDTO(Agent agent) {
        List<MessageSummaryDTO> msgs = agent.getMessages().stream()
                .map(m -> MessageSummaryDTO.builder()
                        .id(m.getId())
                        .dateHeure(m.getDateHeure())
                        .direction(m.getDirection())
                        .sujetEmail(m.getSujetEmail())
                        .resumeIa(m.getResumeIa())
                        .threadIdGmail(m.getThreadIdGmail())
                        .traitePaAgent(true)
                        .build())
                .collect(Collectors.toList());

        List<AnalyseSummaryDTO> analyses = agent.getAnalyses().stream()
                .map(an -> AnalyseSummaryDTO.builder()
                        .id(an.getId())
                        .description(an.getDescription())
                        .dateAnalyse(an.getDateAnalyse())
                        .messageId(an.getMessage().getId())
                        .agentModel(agent.getModel())
                        .build())
                .collect(Collectors.toList());

        return AgentDetailDTO.builder()
                .id(agent.getId())
                .model(agent.getModel())
                .actif(agent.getActif())
                .dateActivation(agent.getDateActivation())
                .messageCount(msgs.size())
                .analyseCount(analyses.size())
                .messages(msgs)
                .analyses(analyses)
                .build();
    }
}
