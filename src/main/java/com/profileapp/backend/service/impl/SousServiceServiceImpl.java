package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.SousServiceImageRequestDTO;
import com.profileapp.backend.dto.request.SousServiceRequestDTO;
import com.profileapp.backend.dto.response.detail.SousServiceDetailDTO;
import com.profileapp.backend.dto.response.summary.SousServiceImageSummaryDTO;
import com.profileapp.backend.dto.response.summary.SousServiceSummaryDTO;
import com.profileapp.backend.entity.SousService;
import com.profileapp.backend.entity.SousServiceImage;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.ServiceRepository;
import com.profileapp.backend.repository.SousServiceImageRepository;
import com.profileapp.backend.repository.SousServiceRepository;
import com.profileapp.backend.service.FileCleanupService;
import com.profileapp.backend.service.SousServiceService;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SousServiceServiceImpl implements SousServiceService {

    private final SousServiceRepository sousServiceRepository;
    private final SousServiceImageRepository sousServiceImageRepository;
    private final ServiceRepository serviceRepository;
    private final FileCleanupService fileCleanupService;
    private final EntityManager entityManager;

    @Override
    @Transactional(readOnly = true)
    public List<SousServiceSummaryDTO> getAllSousServices() {
        return sousServiceRepository.findAllByOrderByOrdreAsc()
                .stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public SousServiceDetailDTO getSousServiceById(Long id) {
        SousService ss = sousServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sous-service non trouvé : " + id));
        return mapToDetailDTO(ss);
    }

    @Override
    @Transactional
    public SousServiceDetailDTO createSousService(SousServiceRequestDTO dto) {
        com.profileapp.backend.entity.Service service = serviceRepository.findById(dto.getServiceId())
                .orElseThrow(() -> new ResourceNotFoundException("Service non trouvé : " + dto.getServiceId()));

        SousService ss = SousService.builder()
                .service(service).nom(dto.getNom())
                .description(dto.getDescription()).prixBase(dto.getPrixBase())
                .ordre(dto.getOrdre() != null ? dto.getOrdre() : 0)
                .actif(dto.getActif() != null ? dto.getActif() : true)
                .build();

        SousService saved = sousServiceRepository.save(ss);

        if (dto.getImageUrls() != null) {
            int ordre = 0;
            for (String url : dto.getImageUrls()) {
                SousServiceImage img = SousServiceImage.builder()
                        .sousService(saved).imageUrl(url).ordre(ordre++).build();
                sousServiceImageRepository.save(img);
            }
        }
        return getSousServiceById(saved.getId());
    }

    @Override
    @Transactional
    public SousServiceDetailDTO updateSousService(Long id, SousServiceRequestDTO dto) {
        SousService ss = sousServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sous-service non trouvé : " + id));

        if (dto.getNom() != null)         ss.setNom(dto.getNom());
        if (dto.getDescription() != null) ss.setDescription(dto.getDescription());
        if (dto.getPrixBase() != null)    ss.setPrixBase(dto.getPrixBase());
        if (dto.getOrdre() != null)       ss.setOrdre(dto.getOrdre());
        if (dto.getActif() != null)       ss.setActif(dto.getActif());

        sousServiceRepository.save(ss);

        if (dto.getImageUrls() != null) {
            /* Récupérer les anciennes URLs pour supprimer les fichiers orphelins */
            List<SousServiceImage> oldImages = sousServiceImageRepository
                    .findBySousServiceIdOrderByOrdreAsc(id);
            List<String> oldUrls = oldImages.stream()
                    .map(SousServiceImage::getImageUrl).collect(Collectors.toList());

            /* Supprimer les anciennes images en base + flush pour éviter les doublons */
            sousServiceImageRepository.deleteBySousServiceId(id);
            entityManager.flush();

            /* Dédupliquer les nouvelles URLs (LinkedHashSet préserve l'ordre) */
            List<String> uniqueUrls = new ArrayList<>(new LinkedHashSet<>(dto.getImageUrls()));

            /* Recréer les images avec les URLs dédupliquées */
            int ordre = 0;
            for (String url : uniqueUrls) {
                SousServiceImage img = SousServiceImage.builder()
                        .sousService(ss).imageUrl(url).ordre(ordre++).build();
                sousServiceImageRepository.save(img);
            }

            /* Supprimer les fichiers physiques des images retirées */
            for (String oldUrl : oldUrls) {
                if (!uniqueUrls.contains(oldUrl)) {
                    fileCleanupService.deleteFile(oldUrl);
                }
            }
        }
        return getSousServiceById(id);
    }

    @Override
    @Transactional
    public void deleteSousService(Long id) {
        SousService ss = sousServiceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Sous-service non trouvé : " + id));

        /* Collecter les URLs des images AVANT suppression en base */
        List<String> imageUrls = (ss.getImages() == null) ? List.of() :
                ss.getImages().stream()
                        .map(SousServiceImage::getImageUrl)
                        .collect(Collectors.toList());

        /* Supprimer en base (images supprimées par orphanRemoval) */
        sousServiceImageRepository.deleteBySousServiceId(id);
        sousServiceRepository.deleteById(id);

        /* Supprimer les fichiers physiques */
        for (String url : imageUrls) {
            fileCleanupService.deleteFile(url);
        }
    }

    @Override
    @Transactional
    public SousServiceImageSummaryDTO addImage(Long sousServiceId, SousServiceImageRequestDTO dto) {
        SousService ss = sousServiceRepository.findById(sousServiceId)
                .orElseThrow(() -> new ResourceNotFoundException("Sous-service non trouvé : " + sousServiceId));
        SousServiceImage img = SousServiceImage.builder()
                .sousService(ss).imageUrl(dto.getImageUrl())
                .ordre(dto.getOrdre() != null ? dto.getOrdre() : 0)
                .build();
        return mapToImageDTO(sousServiceImageRepository.save(img));
    }

    @Override
    @Transactional
    public void deleteImage(Long imageId) {
        SousServiceImage img = sousServiceImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image non trouvée : " + imageId));
        String url = img.getImageUrl();
        sousServiceImageRepository.deleteById(imageId);
        /* Supprimer le fichier physique */
        fileCleanupService.deleteFile(url);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SousServiceSummaryDTO> rechercherParMotCle(String motCle) {
        String q = motCle.trim().toLowerCase();
        return sousServiceRepository.findAllByOrderByOrdreAsc().stream()
                .filter(ss -> (ss.getNom() != null && ss.getNom().toLowerCase().contains(q))
                           || (ss.getDescription() != null && ss.getDescription().toLowerCase().contains(q)))
                .map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SousServiceSummaryDTO> rechercherParFourchettePrix(BigDecimal prixMin, BigDecimal prixMax) {
        return sousServiceRepository.findAllByOrderByOrdreAsc().stream()
                .filter(ss -> Boolean.TRUE.equals(ss.getActif()) && ss.getPrixBase() != null)
                .filter(ss -> (prixMin == null || ss.getPrixBase().compareTo(prixMin) >= 0)
                           && (prixMax == null || ss.getPrixBase().compareTo(prixMax) <= 0))
                .map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SousServiceSummaryDTO> rechercherParServiceEtActif(Long serviceId, Boolean actif) {
        List<SousService> list = actif != null && actif
                ? sousServiceRepository.findByServiceIdAndActifTrueOrderByOrdreAsc(serviceId)
                : sousServiceRepository.findByServiceIdOrderByOrdreAsc(serviceId);
        return list.stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SousServiceSummaryDTO> getAllActifs() {
        return sousServiceRepository.findAllByOrderByOrdreAsc().stream()
                .filter(ss -> Boolean.TRUE.equals(ss.getActif()))
                .map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SousServiceSummaryDTO> getByServiceId(Long serviceId) {
        return sousServiceRepository.findByServiceIdOrderByOrdreAsc(serviceId)
                .stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<SousServiceImageSummaryDTO> getImagesBySousService(Long sousServiceId) {
        return sousServiceImageRepository.findBySousServiceIdOrderByOrdreAsc(sousServiceId)
                .stream().map(this::mapToImageDTO).collect(Collectors.toList());
    }

    // ── MAPPINGS ─────────────────────────────────────────────────────────

    private SousServiceSummaryDTO mapToSummaryDTO(SousService ss) {
        List<SousServiceImageSummaryDTO> images = (ss.getImages() == null) ? List.of() :
                ss.getImages().stream().map(this::mapToImageDTO).collect(Collectors.toList());
        return SousServiceSummaryDTO.builder()
                .id(ss.getId()).serviceId(ss.getService().getId())
                .serviceNom(ss.getService().getNom())
                .nom(ss.getNom()).description(ss.getDescription())
                .prixBase(ss.getPrixBase()).ordre(ss.getOrdre()).actif(ss.getActif())
                .images(images).build();
    }

    private SousServiceDetailDTO mapToDetailDTO(SousService ss) {
        List<SousServiceImageSummaryDTO> images = (ss.getImages() == null) ? List.of() :
                ss.getImages().stream().map(this::mapToImageDTO).collect(Collectors.toList());
        return SousServiceDetailDTO.builder()
                .id(ss.getId()).serviceId(ss.getService().getId())
                .serviceNom(ss.getService().getNom())
                .nom(ss.getNom()).description(ss.getDescription())
                .prixBase(ss.getPrixBase()).ordre(ss.getOrdre()).actif(ss.getActif())
                .images(images).build();
    }

    private SousServiceImageSummaryDTO mapToImageDTO(SousServiceImage img) {
        return SousServiceImageSummaryDTO.builder()
                .id(img.getId()).imageUrl(img.getImageUrl()).ordre(img.getOrdre()).build();
    }
}
