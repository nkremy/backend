package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.ServiceRequestDTO;
import com.profileapp.backend.dto.response.detail.ServiceCatalogueDTO;
import com.profileapp.backend.dto.response.detail.ServiceDetailDTO;
import com.profileapp.backend.dto.response.detail.SousServiceCatalogueDTO;
import com.profileapp.backend.dto.response.summary.LigneCommandeSummaryDTO;
import com.profileapp.backend.dto.response.summary.ServiceSummaryDTO;
import com.profileapp.backend.dto.response.summary.SousServiceImageSummaryDTO;
import com.profileapp.backend.dto.response.summary.SousServiceSummaryDTO;
import com.profileapp.backend.exception.DuplicateResourceException;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.ServiceRepository;
import com.profileapp.backend.service.FileCleanupService;
import com.profileapp.backend.service.ServiceCatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ServiceCatalogueServiceImpl implements ServiceCatalogueService {

    private final ServiceRepository serviceRepository;
    private final FileCleanupService fileCleanupService;

    // ═══════════════════════════════════════════════════════════════
    // CRUD ADMIN
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional
    public ServiceDetailDTO createService(ServiceRequestDTO requestDTO) {
        if (serviceRepository.existsByNom(requestDTO.getNom())) {
            throw new DuplicateResourceException(
                    "Un service avec le nom '" + requestDTO.getNom() + "' existe déjà");
        }
        com.profileapp.backend.entity.Service service = com.profileapp.backend.entity.Service.builder()
                .nom(requestDTO.getNom())
                .prixBase(requestDTO.getPrixBase())
                .description(requestDTO.getDescription())
                .imageUrl(requestDTO.getImageUrl())
                .ordre(requestDTO.getOrdre() != null ? requestDTO.getOrdre() : 0)
                .build();
        com.profileapp.backend.entity.Service saved = serviceRepository.save(service);
        return getServiceById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceSummaryDTO> getAllServices() {
        /* Utilise findAllWithSousServices (JOIN FETCH sousServices)
           Les images sont chargées en lazy dans la transaction */
        return serviceRepository.findAllWithSousServices()
                .stream()
                .map(s -> ServiceSummaryDTO.builder()
                        .id(s.getId())
                        .nom(s.getNom())
                        .prixBase(s.getPrixBase())
                        .description(s.getDescription())
                        .imageUrl(s.getImageUrl())
                        .ordre(s.getOrdre())
                        .sousServicesCount(s.getSousServices().size())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceDetailDTO getServiceById(Long id) {
        com.profileapp.backend.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service non trouvé avec l'id : " + id));
        return mapToDetailDTO(service);
    }

    @Override
    @Transactional
    public ServiceDetailDTO updateService(Long id, ServiceRequestDTO requestDTO) {
        com.profileapp.backend.entity.Service existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service non trouvé avec l'id : " + id));
        if (serviceRepository.existsByNomAndIdNot(requestDTO.getNom(), id)) {
            throw new DuplicateResourceException(
                    "Un service avec le nom '" + requestDTO.getNom() + "' existe déjà");
        }
        existing.setNom(requestDTO.getNom());
        existing.setPrixBase(requestDTO.getPrixBase());
        existing.setDescription(requestDTO.getDescription());
        existing.setImageUrl(requestDTO.getImageUrl());
        if (requestDTO.getOrdre() != null) existing.setOrdre(requestDTO.getOrdre());
        serviceRepository.save(existing);
        return getServiceById(id);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        com.profileapp.backend.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Service non trouvé avec l'id : " + id));

        /* Collecter les URLs des fichiers AVANT suppression */
        String serviceImageUrl = service.getImageUrl();
        java.util.List<String> ssImageUrls = new java.util.ArrayList<>();
        if (service.getSousServices() != null) {
            for (var ss : service.getSousServices()) {
                if (ss.getImages() != null) {
                    ss.getImages().forEach(img -> ssImageUrls.add(img.getImageUrl()));
                }
            }
        }

        serviceRepository.deleteById(id);

        /* Supprimer les fichiers physiques */
        fileCleanupService.deleteFile(serviceImageUrl);
        ssImageUrls.forEach(fileCleanupService::deleteFile);
    }

    // ═══════════════════════════════════════════════════════════════
    // CATALOGUE PUBLIC
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<ServiceCatalogueDTO> getCatalogue() {
        return serviceRepository.findAllWithSousServices()
                .stream()
                .map(this::mapToCatalogueDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceCatalogueDTO getCatalogueById(Long id) {
        com.profileapp.backend.entity.Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service non trouvé avec l'id : " + id));
        return mapToCatalogueDTO(service);
    }

    // ═══════════════════════════════════════════════════════════════
    // RECHERCHE
    // ═══════════════════════════════════════════════════════════════

    @Override
    @Transactional(readOnly = true)
    public List<ServiceSummaryDTO> rechercherParMotCle(String motCle) {
        String q = motCle.trim().toLowerCase();
        return serviceRepository.findAllWithSousServices().stream()
                .filter(s -> (s.getNom() != null && s.getNom().toLowerCase().contains(q))
                          || (s.getDescription() != null && s.getDescription().toLowerCase().contains(q)))
                .map(s -> ServiceSummaryDTO.builder()
                        .id(s.getId()).nom(s.getNom()).prixBase(s.getPrixBase())
                        .description(s.getDescription()).imageUrl(s.getImageUrl())
                        .ordre(s.getOrdre()).sousServicesCount(s.getSousServices().size())
                        .build())
                .collect(Collectors.toList());
    }

    // ═══════════════════════════════════════════════════════════════
    // MAPPINGS
    // ═══════════════════════════════════════════════════════════════

    private ServiceDetailDTO mapToDetailDTO(com.profileapp.backend.entity.Service service) {
        List<LigneCommandeSummaryDTO> lignes = service.getLigneCommandes().stream()
                .map(l -> LigneCommandeSummaryDTO.builder()
                        .id(l.getId())
                        .prixUnitaire(l.getPrixUnitaire())
                        .detailsSpecifiques(l.getDetailsSpecifiques())
                        .dureeEstimation(l.getDureeEstimation())
                        .service(null)
                        .build())
                .collect(Collectors.toList());

        List<SousServiceSummaryDTO> sousServices = service.getSousServices().stream()
                .map(ss -> SousServiceSummaryDTO.builder()
                        .id(ss.getId())
                        .serviceId(service.getId())
                        .serviceNom(service.getNom())
                        .nom(ss.getNom())
                        .description(ss.getDescription())
                        .prixBase(ss.getPrixBase())
                        .ordre(ss.getOrdre())
                        .actif(ss.getActif())
                        .images(ss.getImages().stream()
                                .map(img -> SousServiceImageSummaryDTO.builder()
                                        .id(img.getId())
                                        .imageUrl(img.getImageUrl())
                                        .ordre(img.getOrdre())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return ServiceDetailDTO.builder()
                .id(service.getId())
                .nom(service.getNom())
                .prixBase(service.getPrixBase())
                .description(service.getDescription())
                .imageUrl(service.getImageUrl())
                .ordre(service.getOrdre())
                .utilisationCount(lignes.size())
                .sousServices(sousServices)
                .ligneCommandes(lignes)
                .build();
    }

    private ServiceCatalogueDTO mapToCatalogueDTO(com.profileapp.backend.entity.Service service) {
        List<SousServiceCatalogueDTO> sousServices = service.getSousServices().stream()
                .filter(ss -> Boolean.TRUE.equals(ss.getActif()))
                .map(ss -> SousServiceCatalogueDTO.builder()
                        .id(ss.getId())
                        .nom(ss.getNom())
                        .description(ss.getDescription())
                        .prixBase(ss.getPrixBase())
                        .ordre(ss.getOrdre())
                        .images(ss.getImages().stream()
                                .map(img -> SousServiceImageSummaryDTO.builder()
                                        .id(img.getId())
                                        .imageUrl(img.getImageUrl())
                                        .ordre(img.getOrdre())
                                        .build())
                                .collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());

        return ServiceCatalogueDTO.builder()
                .id(service.getId())
                .nom(service.getNom())
                .description(service.getDescription())
                .prixBase(service.getPrixBase())
                .imageUrl(service.getImageUrl())
                .ordre(service.getOrdre())
                .sousServices(sousServices)
                .build();
    }
}
