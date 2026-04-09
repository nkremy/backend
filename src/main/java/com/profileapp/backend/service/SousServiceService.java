package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.SousServiceImageRequestDTO;
import com.profileapp.backend.dto.request.SousServiceRequestDTO;
import com.profileapp.backend.dto.response.detail.SousServiceDetailDTO;
import com.profileapp.backend.dto.response.summary.SousServiceImageSummaryDTO;
import com.profileapp.backend.dto.response.summary.SousServiceSummaryDTO;

import java.math.BigDecimal;
import java.util.List;

public interface SousServiceService {

    // ── CRUD ─────────────────────────────────────────────────────────────
    SousServiceDetailDTO createSousService(SousServiceRequestDTO requestDTO);
    List<SousServiceSummaryDTO> getAllSousServices();
    List<SousServiceSummaryDTO> getByServiceId(Long serviceId);
    SousServiceDetailDTO getSousServiceById(Long id);
    SousServiceDetailDTO updateSousService(Long id, SousServiceRequestDTO requestDTO);
    void deleteSousService(Long id);

    // ── GESTION DES IMAGES ───────────────────────────────────────────────
    SousServiceImageSummaryDTO addImage(Long sousServiceId, SousServiceImageRequestDTO requestDTO);
    void deleteImage(Long imageId);
    List<SousServiceImageSummaryDTO> getImagesBySousService(Long sousServiceId);

    // ── RECHERCHE ────────────────────────────────────────────────────────
    List<SousServiceSummaryDTO> rechercherParMotCle(String motCle);
    List<SousServiceSummaryDTO> rechercherParFourchettePrix(BigDecimal prixMin, BigDecimal prixMax);
    List<SousServiceSummaryDTO> rechercherParServiceEtActif(Long serviceId, Boolean actif);

    // ── CATALOGUE PUBLIC ─────────────────────────────────────────────────
    // Tous les sous-services actifs, toutes catégories confondues
    List<SousServiceSummaryDTO> getAllActifs();
}
