package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.TicketRequestDTO;
import com.profileapp.backend.dto.response.detail.TicketStatsDTO;
import com.profileapp.backend.dto.response.summary.TicketSummaryDTO;
import com.profileapp.backend.entity.Ticket;
import com.profileapp.backend.entity.TicketPriorite;
import com.profileapp.backend.entity.TicketType;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.TicketRepository;
import com.profileapp.backend.service.TicketService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TicketServiceImpl implements TicketService {

    private final TicketRepository ticketRepository;

    @Override
    @Transactional
    public TicketSummaryDTO createTicket(TicketRequestDTO dto) {
        TicketType type;
        try { type = TicketType.valueOf(dto.getType()); }
        catch (IllegalArgumentException e) { type = TicketType.INFO; }

        TicketPriorite priorite;
        try { priorite = TicketPriorite.valueOf(dto.getPriorite()); }
        catch (IllegalArgumentException e) { priorite = TicketPriorite.BASSE; }

        Ticket ticket = Ticket.builder()
                .titre(dto.getTitre())
                .description(dto.getDescription())
                .type(type)
                .priorite(priorite)
                .agentModel(dto.getAgentModel())
                .lu(false)
                .build();

        Ticket saved = ticketRepository.save(ticket);
        return mapToDTO(saved);
    }

    @Override
    @Transactional(readOnly = true)
    public List<TicketSummaryDTO> getAllTickets(String typeStr, String prioriteStr, Boolean lu) {
        List<Ticket> tickets;

        TicketType type = parseType(typeStr);
        TicketPriorite priorite = parsePriorite(prioriteStr);

        if (type != null && priorite != null) {
            tickets = ticketRepository.findAllByTypeAndPrioriteOrderByDateCreationDesc(type, priorite);
        } else if (type != null) {
            tickets = ticketRepository.findAllByTypeOrderByDateCreationDesc(type);
        } else if (priorite != null) {
            tickets = ticketRepository.findAllByPrioriteOrderByDateCreationDesc(priorite);
        } else if (lu != null && !lu) {
            tickets = ticketRepository.findAllByLuFalseOrderByDateCreationDesc();
        } else {
            tickets = ticketRepository.findAllByOrderByDateCreationDesc();
        }

        return tickets.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public TicketSummaryDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket non trouvé : " + id));
        return mapToDTO(ticket);
    }

    @Override
    @Transactional
    public void markAsRead(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ticket non trouvé : " + id));
        ticket.setLu(true);
        ticketRepository.save(ticket);
    }

    @Override
    @Transactional
    public void markAllAsRead() {
        ticketRepository.markAllAsRead();
    }

    @Override
    @Transactional
    public void deleteTicket(Long id) {
        if (!ticketRepository.existsById(id)) {
            throw new ResourceNotFoundException("Ticket non trouvé : " + id);
        }
        ticketRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TicketStatsDTO getStats() {
        return TicketStatsDTO.builder()
                .total(ticketRepository.count())
                .nonLus(ticketRepository.countByLuFalse())
                .alertesNonLues(ticketRepository.countByLuFalseAndType(TicketType.ALERTE))
                .infosNonLues(ticketRepository.countByLuFalseAndType(TicketType.INFO))
                .build();
    }

    private TicketSummaryDTO mapToDTO(Ticket t) {
        return TicketSummaryDTO.builder()
                .id(t.getId()).titre(t.getTitre()).description(t.getDescription())
                .type(t.getType()).priorite(t.getPriorite()).lu(t.getLu())
                .dateCreation(t.getDateCreation()).agentModel(t.getAgentModel())
                .build();
    }

    private TicketType parseType(String s) {
        if (s == null) return null;
        try { return TicketType.valueOf(s); } catch (Exception e) { return null; }
    }

    private TicketPriorite parsePriorite(String s) {
        if (s == null) return null;
        try { return TicketPriorite.valueOf(s); } catch (Exception e) { return null; }
    }
}
