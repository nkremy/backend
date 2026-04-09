package com.profileapp.backend.controller;

import com.profileapp.backend.dto.request.TicketRequestDTO;
import com.profileapp.backend.dto.response.detail.TicketStatsDTO;
import com.profileapp.backend.dto.response.summary.TicketSummaryDTO;
import com.profileapp.backend.service.TicketService;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tickets")
@RequiredArgsConstructor
public class TicketController {

    private final TicketService ticketService;

    /* POST /api/v1/tickets — créer un ticket (agent IA ou admin) */
    @PostMapping
    public ResponseEntity<ApiResponse<TicketSummaryDTO>> createTicket(
            @Valid @RequestBody TicketRequestDTO requestDTO) {
        TicketSummaryDTO created = ticketService.createTicket(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Ticket créé avec succès", created));
    }

    /* GET /api/v1/tickets?type=ALERTE&priorite=HAUTE&lu=false */
    @GetMapping
    public ResponseEntity<ApiResponse<List<TicketSummaryDTO>>> getAllTickets(
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String priorite,
            @RequestParam(required = false) Boolean lu) {
        return ResponseEntity.ok(ApiResponse.success(
                "Tickets récupérés", ticketService.getAllTickets(type, priorite, lu)));
    }

    /* GET /api/v1/tickets/{id} */
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TicketSummaryDTO>> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Ticket récupéré", ticketService.getTicketById(id)));
    }

    /* PATCH /api/v1/tickets/{id}/lu — marquer comme lu */
    @PatchMapping("/{id}/lu")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long id) {
        ticketService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Ticket marqué comme lu"));
    }

    /* PATCH /api/v1/tickets/lu-tous — marquer tous comme lus */
    @PatchMapping("/lu-tous")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead() {
        ticketService.markAllAsRead();
        return ResponseEntity.ok(ApiResponse.success("Tous les tickets marqués comme lus"));
    }

    /* DELETE /api/v1/tickets/{id} */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteTicket(@PathVariable Long id) {
        ticketService.deleteTicket(id);
        return ResponseEntity.ok(ApiResponse.success("Ticket supprimé"));
    }

    /* GET /api/v1/tickets/stats — compteurs pour le dashboard */
    @GetMapping("/stats")
    public ResponseEntity<ApiResponse<TicketStatsDTO>> getStats() {
        return ResponseEntity.ok(ApiResponse.success(
                "Statistiques tickets", ticketService.getStats()));
    }
}
