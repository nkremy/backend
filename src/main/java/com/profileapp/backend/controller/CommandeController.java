package com.profileapp.backend.controller;

import com.profileapp.backend.dto.request.CommandeRequestDTO;
import com.profileapp.backend.dto.response.detail.CommandeDetailDTO;
import com.profileapp.backend.dto.response.summary.CommandeSummaryDTO;
import com.profileapp.backend.entity.CommandeStatus;
import com.profileapp.backend.service.CommandeService;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/commandes")
@RequiredArgsConstructor
public class CommandeController {

    private final CommandeService commandeService;

    @PostMapping
    public ResponseEntity<ApiResponse<CommandeDetailDTO>> createCommande(
            @Valid @RequestBody CommandeRequestDTO requestDTO) {
        CommandeDetailDTO created = commandeService.createCommande(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Commande créée avec succès", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CommandeSummaryDTO>>> getAllCommandes(
            @RequestParam(required = false) CommandeStatus status) {
        List<CommandeSummaryDTO> commandes = (status != null)
                ? commandeService.getAllCommandesByStatus(status)
                : commandeService.getAllCommandes();
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des commandes récupérée avec succès", commandes));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<CommandeDetailDTO>> getCommandeById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Commande récupérée avec succès", commandeService.getCommandeById(id)));
    }

    @GetMapping("/contact/{contactId}")
    public ResponseEntity<ApiResponse<List<CommandeSummaryDTO>>> getCommandesByContact(
            @PathVariable Long contactId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Commandes du contact récupérées avec succès",
                commandeService.getCommandesByContactId(contactId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<CommandeDetailDTO>> updateCommande(
            @PathVariable Long id,
            @Valid @RequestBody CommandeRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(
                "Commande mise à jour avec succès",
                commandeService.updateCommande(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteCommande(@PathVariable Long id) {
        commandeService.deleteCommande(id);
        return ResponseEntity.ok(ApiResponse.success("Commande supprimée avec succès"));
    }
}
