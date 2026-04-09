package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.TicketRequestDTO;
import com.profileapp.backend.dto.response.detail.TicketStatsDTO;
import com.profileapp.backend.dto.response.summary.TicketSummaryDTO;
import java.util.List;

public interface TicketService {
    TicketSummaryDTO createTicket(TicketRequestDTO requestDTO);
    List<TicketSummaryDTO> getAllTickets(String type, String priorite, Boolean lu);
    TicketSummaryDTO getTicketById(Long id);
    void markAsRead(Long id);
    void markAllAsRead();
    void deleteTicket(Long id);
    TicketStatsDTO getStats();
}
