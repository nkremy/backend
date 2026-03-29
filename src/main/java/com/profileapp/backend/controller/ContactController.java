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

    @GetMapping
    public ResponseEntity<ApiResponse<List<ContactSummaryDTO>>> getAllContacts(
            @RequestParam(required = false) ContactStatus status) {
        List<ContactSummaryDTO> contacts = (status != null)
                ? contactService.getAllContactsByStatus(status)
                : contactService.getAllContacts();
        String message = (status != null)
                ? "Contacts filtrés par statut " + status.name()
                : "Liste des contacts récupérée avec succès";
        return ResponseEntity.ok(ApiResponse.success(message, contacts));
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

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok(ApiResponse.success("Contact supprimé avec succès"));
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
