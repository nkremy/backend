package com.profileapp.backend.service.impl;

import com.profileapp.backend.dto.request.ContactRequestDTO;
import com.profileapp.backend.dto.request.ConvertRequestDTO;
import com.profileapp.backend.dto.response.detail.ContactDetailDTO;
import com.profileapp.backend.dto.response.summary.*;
import com.profileapp.backend.entity.*;
import com.profileapp.backend.exception.DuplicateResourceException;
import com.profileapp.backend.exception.ResourceNotFoundException;
import com.profileapp.backend.repository.ContactRepository;
import com.profileapp.backend.repository.ConversionLogRepository;
import com.profileapp.backend.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ContactServiceImpl implements ContactService {

        private final ContactRepository contactRepository;
        private final ConversionLogRepository conversionLogRepository;

        @Override
        @Transactional
        public ContactDetailDTO createContact(ContactRequestDTO requestDTO) {
                if (contactRepository.existsByEmail(requestDTO.getEmail())) {
                        throw new DuplicateResourceException(
                                        "Un contact avec l'email '" + requestDTO.getEmail() + "' existe déjà");
                }
                Contact contact = Contact.builder()
                                .email(requestDTO.getEmail())
                                .nom(requestDTO.getNom())
                                .telephone(requestDTO.getTelephone())
                                .status(requestDTO.getStatus())
                                .actif(true)
                                .build();
                Echange echange = Echange.builder().build();
                contact.setEchange(echange);
                Contact saved = contactRepository.save(contact);
                return getContactById(saved.getId());
        }

        @Override
        @Transactional(readOnly = true)
        public List<ContactSummaryDTO> getAllContacts() {
                return contactRepository.findAllByActifTrue()
                                .stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<ContactSummaryDTO> getAllContactsByStatus(ContactStatus status) {
                return contactRepository.findAllByActifTrueAndStatus(status)
                                .stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public List<ContactSummaryDTO> getArchivedContacts() {
                return contactRepository.findAllByActifFalse()
                                .stream().map(this::mapToSummaryDTO).collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ContactDetailDTO getContactById(Long id) {
                Contact contact = contactRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Contact non trouvé avec l'id : " + id));
                return mapToDetailDTO(contact);
        }

        @Override
        @Transactional
        public ContactDetailDTO updateContact(Long id, ContactRequestDTO requestDTO) {
                Contact existing = contactRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Contact non trouvé avec l'id : " + id));
                if (contactRepository.existsByEmailAndIdNot(requestDTO.getEmail(), id)) {
                        throw new DuplicateResourceException(
                                        "Un contact avec l'email '" + requestDTO.getEmail() + "' existe déjà");
                }
                existing.setEmail(requestDTO.getEmail());
                existing.setNom(requestDTO.getNom());
                existing.setTelephone(requestDTO.getTelephone());
                existing.setStatus(requestDTO.getStatus());
                contactRepository.save(existing);
                return getContactById(id);
        }

        /* Supprimer = archiver (soft-delete) */
        @Override
        @Transactional
        public void archiveContact(Long id) {
                Contact contact = contactRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Contact non trouvé avec l'id : " + id));
                contact.setActif(false);
                contactRepository.save(contact);
        }

        /* Restaurer un contact archivé */
        @Override
        @Transactional
        public void restoreContact(Long id) {
                Contact contact = contactRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Contact non trouvé avec l'id : " + id));
                contact.setActif(true);
                contactRepository.save(contact);
        }

        @Override
        @Transactional
        public ContactDetailDTO convertToClient(Long id, ConvertRequestDTO requestDTO) {
                Contact contact = contactRepository.findById(id)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Contact non trouvé avec l'id : " + id));
                if (contact.getStatus() == ContactStatus.CLIENT) {
                        throw new DuplicateResourceException("Ce contact est déjà un client");
                }
                contact.setStatus(ContactStatus.CLIENT);
                ConversionLog log = ConversionLog.builder().motif(requestDTO.getMotif()).build();
                contact.addConversionLog(log);
                conversionLogRepository.save(log);
                contactRepository.save(contact);
                return getContactById(id);
        }

        /* Liste légère pour le sélecteur du formulaire message */
        @Override
        @Transactional(readOnly = true)
        public List<ContactSummaryDTO> getContactsForSelect() {
                return contactRepository.findAllByActifTrueOrderByNomAsc()
                                .stream()
                                .map(c -> ContactSummaryDTO.builder()
                                                .id(c.getId())
                                                .email(c.getEmail())
                                                .nom(c.getNom())
                                                .telephone(c.getTelephone())
                                                .status(c.getStatus())
                                                .actif(c.getActif())
                                                .build())
                                .collect(Collectors.toList());
        }

        @Override
        @Transactional(readOnly = true)
        public ContactDetailDTO getContactByEmail(String email) {
                System.err.println("*************************************************");
                System.err.println(" Recherche du contact par email : " + email);
                Contact contact = contactRepository.findByEmail(email)
                                .orElseThrow(() -> new ResourceNotFoundException(
                                                "Contact non trouvé avec l email : " + email));
                if (contact != null) {
                        System.err.println(" Contact trouvé avec échange ID : " + contact.getEmail());
                } else {
                        System.err.println(" Contact trouvé mais sans échange associé");
                }
                return mapToDetailDTO(contact);
        }

        // ── MAPPINGS ──────────────────────────────────────────────────

        private ContactSummaryDTO mapToSummaryDTO(Contact contact) {
                int messageCount = 0;
                if (contact.getEchange() != null) {
                        messageCount = contact.getEchange().getMessages().size();
                }
                return ContactSummaryDTO.builder()
                                .id(contact.getId())
                                .email(contact.getEmail())
                                .nom(contact.getNom())
                                .telephone(contact.getTelephone())
                                .status(contact.getStatus())
                                .actif(contact.getActif())
                                .dateCreation(contact.getDateCreation())
                                .messageCount(messageCount)
                                .commandeCount(contact.getCommandes().size())
                                .aConversionLog(contact.getConversionLog() != null)
                                .build();
        }

        private ContactDetailDTO mapToDetailDTO(Contact contact) {
                EchangeSummaryDTO echangeDTO = null;
                if (contact.getEchange() != null) {
                        Echange echange = contact.getEchange();
                        List<MessageSummaryDTO> msgs = echange.getMessages().stream()
                                        .map(msg -> MessageSummaryDTO.builder()
                                                        .id(msg.getId())
                                                        .dateHeure(msg.getDateHeure())
                                                        .direction(msg.getDirection())
                                                        .sujetEmail(msg.getSujetEmail())
                                                        .resumeIa(msg.getResumeIa())
                                                        .threadIdGmail(msg.getThreadIdGmail())
                                                        .traitePaAgent(msg.getAgent() != null)
                                                        .build())
                                        .collect(Collectors.toList());
                        echangeDTO = EchangeSummaryDTO.builder()
                                        .id(echange.getId())
                                        .messageCount(msgs.size())
                                        .messages(msgs)
                                        .build();
                }

                List<CommandeSummaryDTO> commandesDTOs = contact.getCommandes().stream()
                                .map(cmd -> CommandeSummaryDTO.builder()
                                                .id(cmd.getId())
                                                .dateCommande(cmd.getDateCommande())
                                                .montant(cmd.getMontant())
                                                .status(cmd.getStatus())
                                                .dateLivraisonPrevu(cmd.getDateLivraisonPrevu())
                                                .ligneCount(cmd.getLigneCommandes().size())
                                                .contactEmail(contact.getEmail())
                                                .build())
                                .collect(Collectors.toList());

                ConversionLogSummaryDTO logDTO = null;
                if (contact.getConversionLog() != null) {
                        ConversionLog log = contact.getConversionLog();
                        logDTO = ConversionLogSummaryDTO.builder()
                                        .id(log.getId())
                                        .dateConversion(log.getDateConversion())
                                        .motif(log.getMotif())
                                        .build();
                }

                return ContactDetailDTO.builder()
                                .id(contact.getId())
                                .email(contact.getEmail())
                                .nom(contact.getNom())
                                .telephone(contact.getTelephone())
                                .status(contact.getStatus())
                                .actif(contact.getActif())
                                .dateCreation(contact.getDateCreation())
                                .echange(echangeDTO)
                                .commandes(commandesDTOs)
                                .conversionLog(logDTO)
                                .build();
        }
}
