package com.profileapp.backend.controller;

import com.profileapp.backend.dto.request.SousServiceImageRequestDTO;
import com.profileapp.backend.dto.request.SousServiceRequestDTO;
import com.profileapp.backend.dto.response.detail.SousServiceDetailDTO;
import com.profileapp.backend.dto.response.summary.SousServiceImageSummaryDTO;
import com.profileapp.backend.dto.response.summary.SousServiceSummaryDTO;
import com.profileapp.backend.service.SousServiceService;
import com.profileapp.backend.util.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * CONTROLLER — Gestion admin des sous-services
 * Accès protégé par JWT.
 * Base path : /api/v1/sous-services
 */
@RestController
@RequestMapping("/api/v1/sous-services")
@RequiredArgsConstructor
public class SousServiceController {

    private final SousServiceService sousServiceService;

    // ── CRUD ─────────────────────────────────────────────────────────────

    @PostMapping
    public ResponseEntity<ApiResponse<SousServiceDetailDTO>> createSousService(
            @Valid @RequestBody SousServiceRequestDTO requestDTO) {
        SousServiceDetailDTO created = sousServiceService.createSousService(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Sous-service créé avec succès", created));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SousServiceSummaryDTO>>> getAllSousServices() {
        return ResponseEntity.ok(ApiResponse.success(
                "Liste des sous-services récupérée avec succès",
                sousServiceService.getAllSousServices()));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SousServiceDetailDTO>> getSousServiceById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Sous-service récupéré avec succès",
                sousServiceService.getSousServiceById(id)));
    }

    // ── Sous-services d'un service parent ─────────────────────────────────
    @GetMapping("/par-service/{serviceId}")
    public ResponseEntity<ApiResponse<List<SousServiceSummaryDTO>>> getByServiceId(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(ApiResponse.success(
                "Sous-services du service récupérés avec succès",
                sousServiceService.getByServiceId(serviceId)));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<SousServiceDetailDTO>> updateSousService(
            @PathVariable Long id,
            @Valid @RequestBody SousServiceRequestDTO requestDTO) {
        return ResponseEntity.ok(ApiResponse.success(
                "Sous-service mis à jour avec succès",
                sousServiceService.updateSousService(id, requestDTO)));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteSousService(@PathVariable Long id) {
        sousServiceService.deleteSousService(id);
        return ResponseEntity.ok(ApiResponse.success("Sous-service supprimé avec succès"));
    }

    // ── GESTION DES IMAGES ────────────────────────────────────────────────

    @PostMapping("/{id}/images")
    public ResponseEntity<ApiResponse<SousServiceImageSummaryDTO>> addImage(
            @PathVariable Long id,
            @Valid @RequestBody SousServiceImageRequestDTO requestDTO) {
        SousServiceImageSummaryDTO img = sousServiceService.addImage(id, requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("Image ajoutée avec succès", img));
    }

    @GetMapping("/{id}/images")
    public ResponseEntity<ApiResponse<List<SousServiceImageSummaryDTO>>> getImages(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Images récupérées avec succès",
                sousServiceService.getImagesBySousService(id)));
    }

    @DeleteMapping("/images/{imageId}")
    public ResponseEntity<ApiResponse<Void>> deleteImage(@PathVariable Long imageId) {
        sousServiceService.deleteImage(imageId);
        return ResponseEntity.ok(ApiResponse.success("Image supprimée avec succès"));
    }

    // ── RECHERCHE ─────────────────────────────────────────────────────────

    @GetMapping("/recherche")
    public ResponseEntity<ApiResponse<List<SousServiceSummaryDTO>>> rechercher(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax) {

        if (motCle != null && !motCle.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(
                    "Recherche par mot-clé effectuée",
                    sousServiceService.rechercherParMotCle(motCle)));
        }
        if (prixMin != null || prixMax != null) {
            return ResponseEntity.ok(ApiResponse.success(
                    "Recherche par prix effectuée",
                    sousServiceService.rechercherParFourchettePrix(prixMin, prixMax)));
        }
        return ResponseEntity.ok(ApiResponse.success(
                "Liste complète des sous-services",
                sousServiceService.getAllSousServices()));
    }

    @GetMapping("/par-service/{serviceId}/filtrer")
    public ResponseEntity<ApiResponse<List<SousServiceSummaryDTO>>> filtrerParServiceEtActif(
            @PathVariable Long serviceId,
            @RequestParam(required = false) Boolean actif) {
        return ResponseEntity.ok(ApiResponse.success(
                "Filtrage effectué avec succès",
                sousServiceService.rechercherParServiceEtActif(serviceId, actif)));
    }
}
