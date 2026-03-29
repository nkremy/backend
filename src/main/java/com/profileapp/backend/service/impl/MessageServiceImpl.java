package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.MessageRequestDTO;
import com.profileapp.backend.dto.response.detail.MessageDetailDTO;
import com.profileapp.backend.dto.response.summary.AgentSummaryDTO;
import com.profileapp.backend.dto.response.summary.AnalyseSummaryDTO;
import com.profileapp.backend.dto.response.summary.MessageSummaryDTO;
import com.profileapp.backend.entity.*;
import com.profileapp.backend.exception.DuplicateResourceException;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.AgentRepository;
import com.profileapp.backend.repository.EchangeRepository;
import com.profileapp.backend.repository.MessageRepository;
import com.profileapp.backend.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {

    private final MessageRepository messageRepository;
    private final EchangeRepository echangeRepository;
    private final AgentRepository agentRepository;

    @Override
    @Transactional
    public MessageDetailDTO createMessage(MessageRequestDTO requestDTO) {

        // Vérifier doublon messageIdGmail
        if (requestDTO.getMessageIdGmail() != null
                && messageRepository.existsByMessageIdGmail(requestDTO.getMessageIdGmail())) {
            throw new DuplicateResourceException(
                "Un message avec le messageIdGmail '" + requestDTO.getMessageIdGmail() + "' existe déjà"
            );
        }

        Echange echange = echangeRepository.findById(requestDTO.getEchangeId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Echange non trouvé avec l'id : " + requestDTO.getEchangeId()
                ));

        // Agent null si message humain
        Agent agent = null;
        if (requestDTO.getAgentId() != null) {
            agent = agentRepository.findById(requestDTO.getAgentId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Agent non trouvé avec l'id : " + requestDTO.getAgentId()
                    ));
        }

        Message message = Message.builder()
                .dateHeure(requestDTO.getDateHeure())
                .direction(requestDTO.getDirection())
                .sujetEmail(requestDTO.getSujetEmail())
                .resumeIa(requestDTO.getResumeIa())
                .messageComplet(requestDTO.getMessageComplet())
                .messageIdGmail(requestDTO.getMessageIdGmail())
                .threadIdGmail(requestDTO.getThreadIdGmail())
                .agent(agent)
                .build();

        // helper method → synchronise l'Echange
        echange.addMessage(message);

        Message saved = messageRepository.save(message);
        return getMessageById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<MessageSummaryDTO> getAllMessages() {
        return messageRepository.findAll()
                .stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MessageDetailDTO getMessageById(Long id) {
        Message message = messageRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Message non trouvé avec l'id : " + id
                ));
        return mapToDetailDTO(message);
    }

    @Override
    @Transactional
    public void deleteMessage(Long id) {
        if (!messageRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Message non trouvé avec l'id : " + id
            );
        }
        messageRepository.deleteById(id);
    }

    // ── MAPPINGS PRIVÉS ──────────────────────────────────────────

    private MessageSummaryDTO mapToSummaryDTO(Message message) {
        return MessageSummaryDTO.builder()
                .id(message.getId())
                .dateHeure(message.getDateHeure())
                .direction(message.getDirection())
                .sujetEmail(message.getSujetEmail())
                .resumeIa(message.getResumeIa())
                .threadIdGmail(message.getThreadIdGmail())
                .traitePaAgent(message.getAgent() != null)
                .build();
    }

    private MessageDetailDTO mapToDetailDTO(Message message) {
        AgentSummaryDTO agentDTO = null;
        if (message.getAgent() != null) {
            Agent a = message.getAgent();
            agentDTO = AgentSummaryDTO.builder()
                    .id(a.getId())
                    .model(a.getModel())
                    .actif(a.getActif())
                    .dateActivation(a.getDateActivation())
                    .build();
        }

        AnalyseSummaryDTO analyseDTO = null;
        if (message.getAnalyse() != null) {
            Analyse an = message.getAnalyse();
            analyseDTO = AnalyseSummaryDTO.builder()
                    .id(an.getId())
                    .description(an.getDescription())
                    .dateAnalyse(an.getDateAnalyse())
                    .messageId(message.getId())
                    .agentModel(message.getAgent() != null ? message.getAgent().getModel() : null)
                    .build();
        }

        return MessageDetailDTO.builder()
                .id(message.getId())
                .dateHeure(message.getDateHeure())
                .direction(message.getDirection())
                .sujetEmail(message.getSujetEmail())
                .resumeIa(message.getResumeIa())
                .messageComplet(message.getMessageComplet())
                .messageIdGmail(message.getMessageIdGmail())
                .threadIdGmail(message.getThreadIdGmail())
                .echangeId(message.getEchange().getId())
                .agent(agentDTO)
                .analyse(analyseDTO)
                .build();
    }
}
