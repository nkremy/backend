package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.CommandeRequestDTO;
import com.profileapp.backend.dto.request.LigneCommandeRequestDTO;
import com.profileapp.backend.dto.response.detail.CommandeDetailDTO;
import com.profileapp.backend.dto.response.summary.*;
import com.profileapp.backend.entity.*;
import com.profileapp.backend.entity.CommandeStatus;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.CommandeRepository;
import com.profileapp.backend.repository.ContactRepository;
import com.profileapp.backend.repository.ServiceRepository;
import com.profileapp.backend.repository.SousServiceRepository;
import com.profileapp.backend.service.CommandeService;
import com.profileapp.backend.service.FileCleanupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final ContactRepository contactRepository;
    private final ServiceRepository serviceRepository;
    private final SousServiceRepository sousServiceRepository;
    private final FileCleanupService fileCleanupService;

    @Override
    @Transactional
    public CommandeDetailDTO createCommande(CommandeRequestDTO requestDTO) {
        Contact contact = contactRepository.findById(requestDTO.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Contact non trouvé avec l'id : " + requestDTO.getContactId()));

        CommandeStatus commandeStatus = CommandeStatus.DEVIS;
        if (requestDTO.getStatus() != null) {
            try { commandeStatus = CommandeStatus.valueOf(requestDTO.getStatus()); }
            catch (IllegalArgumentException ignored) { }
        }

        Commande commande = Commande.builder()
                .montant(requestDTO.getMontant())
                .descriptionBesoin(requestDTO.getDescriptionBesoin())
                .ficheDevisUrl(requestDTO.getFicheDevisUrl())
                .dateLivraisonPrevu(requestDTO.getDateLivraisonPrevu())
                .status(commandeStatus)
                .build();
        contact.addCommande(commande);
        addLignes(commande, requestDTO.getLignes());
        Commande saved = commandeRepository.save(commande);
        return getCommandeById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSummaryDTO> getAllCommandes() {
        return commandeRepository.findAll().stream()
                .map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSummaryDTO> getAllCommandesByStatus(CommandeStatus status) {
        return commandeRepository.findAllByStatus(status).stream()
                .map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSummaryDTO> getCommandesByContactId(Long contactId) {
        return commandeRepository.findAllByContactId(contactId).stream()
                .map(this::mapToSummaryDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeDetailDTO getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Commande non trouvée avec l'id : " + id));
        return mapToDetailDTO(commande);
    }

    @Override
    @Transactional
    public CommandeDetailDTO updateCommande(Long id, CommandeRequestDTO requestDTO) {
        Commande existing = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Commande non trouvée avec l'id : " + id));

        existing.setMontant(requestDTO.getMontant());
        existing.setDescriptionBesoin(requestDTO.getDescriptionBesoin());
        existing.setFicheDevisUrl(requestDTO.getFicheDevisUrl());
        existing.setDateLivraisonPrevu(requestDTO.getDateLivraisonPrevu());

        /* ═══ NOUVEAU — mise à jour du statut ═══ */
        if (requestDTO.getStatus() != null) {
            try {
                existing.setStatus(CommandeStatus.valueOf(requestDTO.getStatus()));
            } catch (IllegalArgumentException ignored) { }
        }

        /* ═══ NOUVEAU — mise à jour des lignes de commande ═══ */
        if (requestDTO.getLignes() != null) {
            existing.getLigneCommandes().clear();
            addLignes(existing, requestDTO.getLignes());
        }

        commandeRepository.save(existing);
        return getCommandeById(id);
    }

    @Override
    @Transactional
    public void deleteCommande(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Commande non trouvée avec l'id : " + id));

        /* Collecter l'URL du fichier devis AVANT suppression */
        String ficheUrl = commande.getFicheDevisUrl();

        commandeRepository.deleteById(id);

        /* Supprimer le fichier physique */
        fileCleanupService.deleteFile(ficheUrl);
    }

    // ── UTILITAIRE — ajout de lignes à une commande ───────────────────

    private void addLignes(Commande commande, List<LigneCommandeRequestDTO> lignesDTO) {
        if (lignesDTO == null) return;
        for (LigneCommandeRequestDTO ligneDTO : lignesDTO) {
            com.profileapp.backend.entity.Service service = serviceRepository.findById(ligneDTO.getServiceId())
                    .orElseThrow(() -> new ResourceNotFoundException(
                        "Service non trouvé avec l'id : " + ligneDTO.getServiceId()));
            SousService sousService = null;
            if (ligneDTO.getSousServiceId() != null) {
                sousService = sousServiceRepository.findById(ligneDTO.getSousServiceId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                            "Sous-service non trouvé avec l'id : " + ligneDTO.getSousServiceId()));
            }
            LigneCommande ligne = LigneCommande.builder()
                    .prixUnitaire(ligneDTO.getPrixUnitaire())
                    .detailsSpecifiques(ligneDTO.getDetailsSpecifiques())
                    .dureeEstimation(ligneDTO.getDureeEstimation())
                    .service(service)
                    .sousService(sousService)
                    .build();
            commande.addLigneCommande(ligne);
        }
    }

    // ── MAPPINGS ──────────────────────────────────────────────────────

    private CommandeSummaryDTO mapToSummaryDTO(Commande commande) {
        return CommandeSummaryDTO.builder()
                .id(commande.getId())
                .dateCommande(commande.getDateCommande())
                .montant(commande.getMontant())
                .status(commande.getStatus())
                .dateLivraisonPrevu(commande.getDateLivraisonPrevu())
                .ligneCount(commande.getLigneCommandes().size())
                .contactEmail(commande.getContact().getEmail())
                .build();
    }

    private CommandeDetailDTO mapToDetailDTO(Commande commande) {
        List<LigneCommandeSummaryDTO> lignes = commande.getLigneCommandes().stream()
                .map(l -> {
                    ServiceSummaryDTO serviceDTO = ServiceSummaryDTO.builder()
                            .id(l.getService().getId())
                            .nom(l.getService().getNom())
                            .prixBase(l.getService().getPrixBase())
                            .description(l.getService().getDescription())
                            .imageUrl(l.getService().getImageUrl())
                            .ordre(l.getService().getOrdre())
                            .build();
                    SousServiceSummaryDTO sousServiceDTO = null;
                    if (l.getSousService() != null) {
                        sousServiceDTO = SousServiceSummaryDTO.builder()
                                .id(l.getSousService().getId())
                                .serviceId(l.getService().getId())
                                .serviceNom(l.getService().getNom())
                                .nom(l.getSousService().getNom())
                                .description(l.getSousService().getDescription())
                                .prixBase(l.getSousService().getPrixBase())
                                .ordre(l.getSousService().getOrdre())
                                .actif(l.getSousService().getActif())
                                .build();
                    }
                    return LigneCommandeSummaryDTO.builder()
                            .id(l.getId())
                            .prixUnitaire(l.getPrixUnitaire())
                            .detailsSpecifiques(l.getDetailsSpecifiques())
                            .dureeEstimation(l.getDureeEstimation())
                            .service(serviceDTO)
                            .sousService(sousServiceDTO)
                            .build();
                }).collect(Collectors.toList());

        Contact c = commande.getContact();
        ContactSummaryDTO contactDTO = ContactSummaryDTO.builder()
                .id(c.getId()).email(c.getEmail()).nom(c.getNom())
                .telephone(c.getTelephone()).status(c.getStatus())
                .actif(c.getActif())
                .dateCreation(c.getDateCreation())
                .commandeCount(0).messageCount(0).aConversionLog(false)
                .build();

        return CommandeDetailDTO.builder()
                .id(commande.getId())
                .dateCommande(commande.getDateCommande())
                .montant(commande.getMontant())
                .status(commande.getStatus())
                .descriptionBesoin(commande.getDescriptionBesoin())
                .ficheDevisUrl(commande.getFicheDevisUrl())
                .dateLivraisonPrevu(commande.getDateLivraisonPrevu())
                .contact(contactDTO)
                .ligneCommandes(lignes)
                .build();
    }
}
