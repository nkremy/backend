package com.profileapp.backend.controller;

import com.profileapp.backend.dto.response.detail.ServiceCatalogueDTO;
import com.profileapp.backend.dto.response.summary.SousServiceSummaryDTO;
import com.profileapp.backend.service.ServiceCatalogueService;
import com.profileapp.backend.service.SousServiceService;
import com.profileapp.backend.util.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

/**
 * CONTROLLER — Catalogue public
 *
 * Endpoints accessibles SANS authentification.
 * Utilisés par :
 *   - Le frontend (page catalogue consultable sans compte)
 *   - L'agent IA (pour présenter les offres aux prospects)
 *
 * Base path : /api/v1/catalogue
 *
 * IMPORTANT : Routes whitelistées dans SecurityConfig.
 */
@RestController
@RequestMapping("/api/v1/catalogue")
@RequiredArgsConstructor
public class CatalogueController {

    private final ServiceCatalogueService serviceCatalogueService;
    private final SousServiceService sousServiceService;

    // ── Catalogue complet — tous les services avec sous-services actifs ──
    @GetMapping
    public ResponseEntity<ApiResponse<List<ServiceCatalogueDTO>>> getCatalogue() {
        return ResponseEntity.ok(ApiResponse.success(
                "Catalogue récupéré avec succès",
                serviceCatalogueService.getCatalogue()));
    }

    // ── Un service précis avec ses sous-services actifs ───────────────────
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ServiceCatalogueDTO>> getCatalogueById(
            @PathVariable Long id) {
        return ResponseEntity.ok(ApiResponse.success(
                "Service récupéré avec succès",
                serviceCatalogueService.getCatalogueById(id)));
    }

    // ── Recherche dans le catalogue ───────────────────────────────────────
    // GET /api/v1/catalogue/recherche?motCle=site
    // GET /api/v1/catalogue/recherche?prixMin=50000&prixMax=500000
    // GET /api/v1/catalogue/recherche        → tous les sous-services actifs
    @GetMapping("/recherche")
    public ResponseEntity<ApiResponse<List<SousServiceSummaryDTO>>> rechercherDansCatalogue(
            @RequestParam(required = false) String motCle,
            @RequestParam(required = false) BigDecimal prixMin,
            @RequestParam(required = false) BigDecimal prixMax) {

        if (motCle != null && !motCle.isBlank()) {
            return ResponseEntity.ok(ApiResponse.success(
                    "Résultats de la recherche par mot-clé",
                    sousServiceService.rechercherParMotCle(motCle)));
        }
        if (prixMin != null || prixMax != null) {
            return ResponseEntity.ok(ApiResponse.success(
                    "Résultats de la recherche par prix",
                    sousServiceService.rechercherParFourchettePrix(prixMin, prixMax)));
        }
        // Aucun critère → tous les sous-services actifs
        return ResponseEntity.ok(ApiResponse.success(
                "Tous les sous-services actifs",
                sousServiceService.getAllActifs()));
    }
}
