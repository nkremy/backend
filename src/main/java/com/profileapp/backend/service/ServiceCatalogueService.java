package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.ServiceRequestDTO;
import com.profileapp.backend.dto.response.detail.ServiceCatalogueDTO;
import com.profileapp.backend.dto.response.detail.ServiceDetailDTO;
import com.profileapp.backend.dto.response.summary.ServiceSummaryDTO;

import java.util.List;

public interface ServiceCatalogueService {

    // ── CRUD admin ───────────────────────────────────────────────────────
    ServiceDetailDTO createService(ServiceRequestDTO requestDTO);
    List<ServiceSummaryDTO> getAllServices();
    ServiceDetailDTO getServiceById(Long id);
    ServiceDetailDTO updateService(Long id, ServiceRequestDTO requestDTO);
    void deleteService(Long id);

    // ── CATALOGUE PUBLIC ─────────────────────────────────────────────────
    List<ServiceCatalogueDTO> getCatalogue();
    ServiceCatalogueDTO getCatalogueById(Long id);

    // ── RECHERCHE ────────────────────────────────────────────────────────
    List<ServiceSummaryDTO> rechercherParMotCle(String motCle);
}
