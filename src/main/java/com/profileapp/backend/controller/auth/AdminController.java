package com.profileapp.backend.controller.auth;

import com.profileapp.backend.dto.auth.ApiKeyRequestDTO;
import com.profileapp.backend.dto.auth.ApiKeyResponseDTO;
import com.profileapp.backend.entity.auth.ApiKey;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.auth.ApiKeyRepository;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ApiKeyRepository apiKeyRepository;

    // ════════════════════════════════════════════════════════════════
    // GET /api/v1/admin/me
    //
    // @AuthenticationPrincipal UserDetails user :
    //   Spring lit SecurityContextHolder.getContext().getAuthentication()
    //   récupère le principal (UserDetails mis par JwtFilter)
    //   et l'injecte ici automatiquement — pas besoin d'appeler
    //   SecurityContextHolder manuellement.
    // ════════════════════════════════════════════════════════════════
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<String>> me(
            @AuthenticationPrincipal UserDetails user) {

        return ResponseEntity.ok(
                ApiResponse.success("Admin connecté", user.getUsername()));
    }

    // ════════════════════════════════════════════════════════════════
    // POST /api/v1/admin/api-keys
    //
    // Crée une nouvelle clé API pour un agent.
    // La valeur de la clé est générée ici (UUID préfixé) et retournée
    // UNE SEULE FOIS — après, elle n'est plus jamais affichée.
    // ════════════════════════════════════════════════════════════════
    @PostMapping("/api-keys")
    public ResponseEntity<ApiResponse<ApiKeyResponseDTO>> createApiKey(
            @Valid @RequestBody ApiKeyRequestDTO requestDTO) {

        // Générer une clé unique avec préfixe lisible
        // Format : gno-agent-xxxxxxxx-xxxx-xxxx-xxxx-xxxxxxxxxxxx
        String cleApi = "gno-agent-" + UUID.randomUUID();

        ApiKey apiKey = ApiKey.builder()
                .nom(requestDTO.getNom())
                .cleApi(cleApi)
                .actif(true)
                .expiresAt(requestDTO.getExpiresAt())
                .build();

        apiKeyRepository.save(apiKey);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("Clé API créée", toResponse(apiKey)));
    }

    // ════════════════════════════════════════════════════════════════
    // GET /api/v1/admin/api-keys
    //
    // Liste toutes les clés actives.
    // Note : cleApi est masquée dans la liste — visible uniquement
    // à la création. Ici on affiche seulement les métadonnées.
    // ════════════════════════════════════════════════════════════════
    @GetMapping("/api-keys")
    public ResponseEntity<ApiResponse<List<ApiKeyResponseDTO>>> listApiKeys() {

        List<ApiKeyResponseDTO> keys = apiKeyRepository.findAllByActifTrue()
                .stream()
                .map(this::toResponseMasked)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                ApiResponse.success(keys.size() + " clé(s) active(s)", keys));
    }

    // ════════════════════════════════════════════════════════════════
    // DELETE /api/v1/admin/api-keys/{id}
    //
    // Révoque une clé — actif passe à false.
    // L'agent utilisant cette clé recevra 401 à sa prochaine requête.
    // On ne supprime pas la ligne — on garde la trace.
    // ════════════════════════════════════════════════════════════════
    @DeleteMapping("/api-keys/{id}")
    public ResponseEntity<ApiResponse<Void>> revokeApiKey(@PathVariable Long id) {

        ApiKey apiKey = apiKeyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Clé API non trouvée avec l'id : " + id));

        apiKey.setActif(false);
        apiKeyRepository.save(apiKey);

        return ResponseEntity.ok(ApiResponse.success("Clé API révoquée"));
    }

    // ── Mappers privés ────────────────────────────────────────────────

    // Version complète — utilisée à la création (cleApi visible une seule fois)
    private ApiKeyResponseDTO toResponse(ApiKey k) {
        return ApiKeyResponseDTO.builder()
                .id(k.getId())
                .nom(k.getNom())
                .cleApi(k.getCleApi())
                .actif(k.isActif())
                .createdAt(k.getCreatedAt())
                .expiresAt(k.getExpiresAt())
                .build();
    }

    // Version masquée — utilisée dans la liste (cleApi cachée)
    private ApiKeyResponseDTO toResponseMasked(ApiKey k) {
        return ApiKeyResponseDTO.builder()
                .id(k.getId())
                .nom(k.getNom())
                .cleApi("gno-agent-****")   // masquée
                .actif(k.isActif())
                .createdAt(k.getCreatedAt())
                .expiresAt(k.getExpiresAt())
                .build();
    }
}
