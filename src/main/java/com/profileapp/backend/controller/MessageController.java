package com.profileapp.backend.controller;

import com.profileapp.backend.dto.request.MessageRequestDTO;
import com.profileapp.backend.dto.response.detail.MessageDetailDTO;
import com.profileapp.backend.dto.response.summary.MessageSummaryDTO;
import com.profileapp.backend.service.MessageService;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;

    @PostMapping
    public ResponseEntity<ApiResponse<MessageDetailDTO>> createMessage(
            @Valid @RequestBody MessageRequestDTO requestDTO) {
        MessageDetailDTO created = messageService.createMessage(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Message enregistré avec succès", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<MessageSummaryDTO>>> getAllMessages() {
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des messages récupérée avec succès",
                messageService.getAllMessages()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<MessageDetailDTO>> getMessageById(@PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Message récupéré avec succès", messageService.getMessageById(id)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteMessage(@PathVariable Long id) {
        messageService.deleteMessage(id);
        return ResponseEntity.ok(ApiResponse.success("Message supprimé avec succès"));
    }
}
