package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.ServiceRequestDTO;
import com.profileapp.backend.dto.response.detail.ServiceDetailDTO;
import com.profileapp.backend.dto.response.summary.LigneCommandeSummaryDTO;
import com.profileapp.backend.dto.response.summary.ServiceSummaryDTO;
import com.profileapp.backend.entity.Service;
import com.profileapp.backend.exception.DuplicateResourceException;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.ServiceRepository;
import com.profileapp.backend.service.ServiceCatalogueService;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@org.springframework.stereotype.Service
@RequiredArgsConstructor
public class ServiceCatalogueServiceImpl implements ServiceCatalogueService {

    private final ServiceRepository serviceRepository;

    @Override
    @Transactional
    public ServiceDetailDTO createService(ServiceRequestDTO requestDTO) {

        if (serviceRepository.existsByNom(requestDTO.getNom())) {
            throw new DuplicateResourceException(
                    "Un service avec le nom '" + requestDTO.getNom() + "' existe déjà");
        }

        Service service = com.profileapp.backend.entity.Service.builder()
                .nom(requestDTO.getNom())
                .prixBase(requestDTO.getPrixBase())
                .description(requestDTO.getDescription())
                .build();

        Service saved = serviceRepository.save(service);
        return getServiceById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ServiceSummaryDTO> getAllServices() {
        return serviceRepository.findAll()
                .stream()
                .map(s -> ServiceSummaryDTO.builder()
                        .id(s.getId())
                        .nom(s.getNom())
                        .prixBase(s.getPrixBase())
                        .description(s.getDescription())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ServiceDetailDTO getServiceById(Long id) {
        Service service = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service non trouvé avec l'id : " + id));
        return mapToDetailDTO(service);
    }

    @Override
    @Transactional
    public ServiceDetailDTO updateService(Long id, ServiceRequestDTO requestDTO) {

        Service existing = serviceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Service non trouvé avec l'id : " + id));

        if (serviceRepository.existsByNomAndIdNot(requestDTO.getNom(), id)) {
            throw new DuplicateResourceException(
                    "Un service avec le nom '" + requestDTO.getNom() + "' existe déjà");
        }

        existing.setNom(requestDTO.getNom());
        existing.setPrixBase(requestDTO.getPrixBase());
        existing.setDescription(requestDTO.getDescription());

        serviceRepository.save(existing);
        return getServiceById(id);
    }

    @Override
    @Transactional
    public void deleteService(Long id) {
        if (!serviceRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                    "Service non trouvé avec l'id : " + id);
        }
        serviceRepository.deleteById(id);
    }

    private ServiceDetailDTO mapToDetailDTO(Service service) {
        List<LigneCommandeSummaryDTO> lignes = service.getLigneCommandes().stream()
                .map(l -> LigneCommandeSummaryDTO.builder()
                        .id(l.getId())
                        .prixUnitaire(l.getPrixUnitaire())
                        .detailsSpecifiques(l.getDetailsSpecifiques())
                        .dureeEstimation(l.getDureeEstimation())
                        .service(null)
                        .build())
                .collect(Collectors.toList());

        return ServiceDetailDTO.builder()
                .id(service.getId())
                .nom(service.getNom())
                .prixBase(service.getPrixBase())
                .description(service.getDescription())
                .utilisationCount(lignes.size())
                .ligneCommandes(lignes)
                .build();
    }
}
