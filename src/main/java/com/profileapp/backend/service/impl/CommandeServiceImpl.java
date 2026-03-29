package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.CommandeRequestDTO;
import com.profileapp.backend.dto.request.LigneCommandeRequestDTO;
import com.profileapp.backend.dto.response.detail.CommandeDetailDTO;
import com.profileapp.backend.dto.response.summary.*;
import com.profileapp.backend.entity.*;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.CommandeRepository;
import com.profileapp.backend.repository.ContactRepository;
import com.profileapp.backend.repository.ServiceRepository;
import com.profileapp.backend.service.CommandeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommandeServiceImpl implements CommandeService {

    private final CommandeRepository commandeRepository;
    private final ContactRepository contactRepository;
    private final ServiceRepository serviceRepository;

    @Override
    @Transactional
    public CommandeDetailDTO createCommande(CommandeRequestDTO requestDTO) {

        Contact contact = contactRepository.findById(requestDTO.getContactId())
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Contact non trouvé avec l'id : " + requestDTO.getContactId()
                ));

        Commande commande = Commande.builder()
                .montant(requestDTO.getMontant())
                .descriptionBesoin(requestDTO.getDescriptionBesoin())
                .ficheDevisUrl(requestDTO.getFicheDevisUrl())
                .dateLivraisonPrevu(requestDTO.getDateLivraisonPrevu())
                .build();

        // Lier au contact via helper method
        contact.addCommande(commande);

        // Créer les lignes si présentes
        if (requestDTO.getLignes() != null) {
            for (LigneCommandeRequestDTO ligneDTO : requestDTO.getLignes()) {
                com.profileapp.backend.entity.Service service = serviceRepository.findById(ligneDTO.getServiceId())
                        .orElseThrow(() -> new ResourceNotFoundException(
                            "Service non trouvé avec l'id : " + ligneDTO.getServiceId()
                        ));
                LigneCommande ligne = LigneCommande.builder()
                        .prixUnitaire(ligneDTO.getPrixUnitaire())
                        .detailsSpecifiques(ligneDTO.getDetailsSpecifiques())
                        .dureeEstimation(ligneDTO.getDureeEstimation())
                        .service(service)
                        .build();
                commande.addLigneCommande(ligne);
            }
        }

        Commande saved = commandeRepository.save(commande);
        return getCommandeById(saved.getId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSummaryDTO> getAllCommandes() {
        return commandeRepository.findAll()
                .stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSummaryDTO> getAllCommandesByStatus(CommandeStatus status) {
        return commandeRepository.findAllByStatus(status)
                .stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommandeSummaryDTO> getCommandesByContactId(Long contactId) {
        return commandeRepository.findAllByContactId(contactId)
                .stream()
                .map(this::mapToSummaryDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommandeDetailDTO getCommandeById(Long id) {
        Commande commande = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Commande non trouvée avec l'id : " + id
                ));
        return mapToDetailDTO(commande);
    }

    @Override
    @Transactional
    public CommandeDetailDTO updateCommande(Long id, CommandeRequestDTO requestDTO) {

        Commande existing = commandeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                    "Commande non trouvée avec l'id : " + id
                ));

        existing.setMontant(requestDTO.getMontant());
        existing.setDescriptionBesoin(requestDTO.getDescriptionBesoin());
        existing.setFicheDevisUrl(requestDTO.getFicheDevisUrl());
        existing.setDateLivraisonPrevu(requestDTO.getDateLivraisonPrevu());

        commandeRepository.save(existing);
        return getCommandeById(id);
    }

    @Override
    @Transactional
    public void deleteCommande(Long id) {
        if (!commandeRepository.existsById(id)) {
            throw new ResourceNotFoundException(
                "Commande non trouvée avec l'id : " + id
            );
        }
        commandeRepository.deleteById(id);
    }

    // ── MAPPINGS PRIVÉS ──────────────────────────────────────────

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
                .map(l -> LigneCommandeSummaryDTO.builder()
                        .id(l.getId())
                        .prixUnitaire(l.getPrixUnitaire())
                        .detailsSpecifiques(l.getDetailsSpecifiques())
                        .dureeEstimation(l.getDureeEstimation())
                        .service(ServiceSummaryDTO.builder()
                                .id(l.getService().getId())
                                .nom(l.getService().getNom())
                                .prixBase(l.getService().getPrixBase())
                                .description(l.getService().getDescription())
                                .build())
                        .build())
                .collect(Collectors.toList());

        Contact c = commande.getContact();
        ContactSummaryDTO contactDTO = ContactSummaryDTO.builder()
                .id(c.getId())
                .email(c.getEmail())
                .nom(c.getNom())
                .telephone(c.getTelephone())
                .status(c.getStatus())
                .dateCreation(c.getDateCreation())
                .commandeCount(0)
                .messageCount(0)
                .aConversionLog(false)
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
