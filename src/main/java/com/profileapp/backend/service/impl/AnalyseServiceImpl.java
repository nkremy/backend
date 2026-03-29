package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.AnalyseRequestDTO;
import com.profileapp.backend.dto.response.detail.AnalyseDetailDTO;
import com.profileapp.backend.dto.response.summary.AgentSummaryDTO;
import com.profileapp.backend.dto.response.summary.AnalyseSummaryDTO;
import com.profileapp.backend.dto.response.summary.MessageSummaryDTO;
import com.profileapp.backend.entity.Agent;
import com.profileapp.backend.entity.Analyse;
import com.profileapp.backend.entity.Message;
import com.profileapp.backend.exception.DuplicateResourceException;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.AgentRepository;
import com.profileapp.backend.repository.AnalyseRepository;
import com.profileapp.backend.repository.MessageRepository;
import com.profileapp.backend.service.AnalyseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AnalyseServiceImpl implements AnalyseService {

    private final AnalyseRepository analyseRepository;
    private final MessageRepository messageRepository;
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public AnalyseDetailDTO createAnalyse(AnalyseRequestDTO requestDTO) {

        // Un message ne peut avoir qu'une seule analyse
        if (analyseRepository.existsByMessageId(requestDTO.getMessageId())) {
            throw new DuplicateResourceException(
                "Une analyse existe déjà pour le message id : " + requestDTO.getMessageId()
            );
        }

        Message message = messageRepository.findById(requestDTO.getMessageId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Message non trouvé avec l'id : " + requestDTO.getMessageId()
                ));

        Agent agent = agentRepository.findById(requestDTO.getAgentId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Agent non trouvé avec l'id : " + requestDTO.getAgentId()
                ));

        Analyse analyse = Analyse.builder()
                .description(requestDTO.getDescription())
                .message(message)
                .agent(agent)
                .build();

        Analyse saved = analyseRepository.save(analyse);
        return getAnalyseById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AnalyseSummaryDTO> getAllAnalyses() {
        return analyseRepository.findAll()
                .stream()
                .map(an -> AnalyseSummaryDTO.builder()
                        .id(an.getId())
                        .description(an.getDescription())
                        .dateAnalyse(an.getDateAnalyse())
                        .messageId(an.getMessage().getId())
                        .agentModel(an.getAgent().getModel())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AnalyseDetailDTO getAnalyseById(Long id) {
        Analyse analyse = analyseRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Analyse non trouvée avec l'id : " + id
                ));

        Message m = analyse.getMessage();
        Agent a = analyse.getAgent();

        MessageSummaryDTO msgDTO = MessageSummaryDTO.builder()
                .id(m.getId())
                .dateHeure(m.getDateHeure())
                .direction(m.getDirection())
                .sujetEmail(m.getSujetEmail())
                .resumeIa(m.getResumeIa())
                .threadIdGmail(m.getThreadIdGmail())
                .traitePaAgent(m.getAgent() != null)
                .build();

        AgentSummaryDTO agentDTO = AgentSummaryDTO.builder()
                .id(a.getId())
                .model(a.getModel())
                .actif(a.getActif())
                .dateActivation(a.getDateActivation())
                .build();

        return AnalyseDetailDTO.builder()
                .id(analyse.getId())
                .description(analyse.getDescription())
                .dateAnalyse(analyse.getDateAnalyse())
                .message(msgDTO)
                .agent(agentDTO)
                .build();
    }

    @Override
    @Transactional
    public void deleteAnalyse(Long id) {
        if (!analyseRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Analyse non trouvée avec l'id : " + id
            );
        }
        analyseRepository.deleteById(id);
    }
}
