package com.profileapp.backend.controller;

import com.profileapp.backend.dto.request.ContactRequestDTO;
import com.profileapp.backend.dto.request.ConvertRequestDTO;
import com.profileapp.backend.dto.response.detail.ContactDetailDTO;
import com.profileapp.backend.dto.response.summary.ContactSummaryDTO;
import com.profileapp.backend.entity.ContactStatus;
import com.profileapp.backend.service.ContactService;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ApiResponse<ContactDetailDTO>> createContact(
            @Valid @RequestBody ContactRequestDTO requestDTO) {
        ContactDetailDTO created = contactService.createContact(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Contact créé avec succès", created));
    }

    /* GET /api/v1/contacts              → tous les actifs
       GET /api/v1/contacts?status=CLIENT → actifs filtrés par statut */
    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactSummaryDTO>>> getAllContacts(
            @RequestParam(required = false) ContactStatus status) {
        List<ContactSummaryDTO> contacts = (status != null)
                ? contactService.getAllContactsByStatus(status)
                : contactService.getAllContacts();
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des contacts récupérée avec succès", contacts));
    }

    /* GET /api/v1/contacts/archived → contacts archivés */
    @GetMapping("/archived")
    public ResponseEntity<ApiResponse<List<ContactSummaryDTO>>> getArchivedContacts() {
        return ResponseEntity.ok(ApiResponse.success(
                "Contacts archivés récupérés avec succès",
                contactService.getArchivedContacts()));
    }

    /* GET /api/v1/contacts/select → liste légère pour sélecteur formulaire message */
    @GetMapping("/select")
    public ResponseEntity<ApiResponse<List<ContactSummaryDTO>>> getContactsForSelect() {
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des contacts pour sélection",
                contactService.getContactsForSelect()));
    }

    /* GET /api/v1/contacts/search?email=xxx */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<ContactDetailDTO>> getContactByEmail(
            @RequestParam String email) {
        return ResponseEntity.ok(ApiResponse.success(
                "Contact trouvé", contactService.getContactByEmail(email)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactDetailDTO>> getContactById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Contact récupéré avec succès", contactService.getContactById(id)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ContactDetailDTO>> updateContact(
            @PathVariable Long id,
            @Valid @RequestBody ContactRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(
                "Contact mis à jour avec succès", contactService.updateContact(id, requestDTO)));
    }

    /* DELETE = archivage (soft-delete) */
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> archiveContact(@PathVariable Long id) {
        contactService.archiveContact(id);
        return ResponseEntity.ok(ApiResponse.success("Contact archivé avec succès"));
    }

    /* POST /api/v1/contacts/{id}/restore → restaurer un contact archivé */
    @PostMapping("/{id}/restore")
    public ResponseEntity<ApiResponse<Void>> restoreContact(@PathVariable Long id) {
        contactService.restoreContact(id);
        return ResponseEntity.ok(ApiResponse.success("Contact restauré avec succès"));
    }

    @PostMapping("/{id}/convert")
    public ResponseEntity<ApiResponse<ContactDetailDTO>> convertToClient(
            @PathVariable Long id,
            @Valid @RequestBody ConvertRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(
                "Contact converti en client avec succès",
                contactService.convertToClient(id, requestDTO)));
    }
}
