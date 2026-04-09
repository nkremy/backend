package com.profileapp.backend.controller;

import com.profileapp.backend.dto.request.AgentRequestDTO;
import com.profileapp.backend.dto.response.detail.AgentDetailDTO;
import com.profileapp.backend.dto.response.summary.AgentSummaryDTO;
import com.profileapp.backend.service.AgentService;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping
    public ResponseEntity<ApiResponse<AgentDetailDTO>> createAgent(
            @Valid @RequestBody AgentRequestDTO requestDTO) {
        AgentDetailDTO created = agentService.createAgent(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Agent créé avec succès", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<AgentSummaryDTO>>> getAllAgents() {
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des agents récupérée avec succès",
                agentService.getAllAgents()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<AgentDetailDTO>> getAgentById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Agent récupéré avec succès", agentService.getAgentById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<AgentDetailDTO>> updateAgent(
            @PathVariable Long id,
            @Valid @RequestBody AgentRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(
                "Agent mis à jour avec succès", agentService.updateAgent(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteAgent(@PathVariable Long id) {
        agentService.deleteAgent(id);
        return ResponseEntity.ok(ApiResponse.success("Agent supprimé avec succès"));
    }
}
