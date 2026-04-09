package com.profileapp.backend.service;

import com.profileapp.backend.dto.request.ContactRequestDTO;
import com.profileapp.backend.dto.request.ConvertRequestDTO;
import com.profileapp.backend.dto.response.detail.ContactDetailDTO;
import com.profileapp.backend.dto.response.summary.ContactSummaryDTO;
import com.profileapp.backend.entity.ContactStatus;
import java.util.List;

public interface ContactService {
    ContactDetailDTO createContact(ContactRequestDTO requestDTO);
    List<ContactSummaryDTO> getAllContacts();
    List<ContactSummaryDTO> getAllContactsByStatus(ContactStatus status);
    List<ContactSummaryDTO> getArchivedContacts();
    ContactDetailDTO getContactById(Long id);
    ContactDetailDTO updateContact(Long id, ContactRequestDTO requestDTO);
    void archiveContact(Long id);
    void restoreContact(Long id);
    ContactDetailDTO convertToClient(Long id, ConvertRequestDTO requestDTO);
    /* Liste légère pour le sélecteur du formulaire message */
    List<ContactSummaryDTO> getContactsForSelect();
    ContactDetailDTO getContactByEmail(String email);
}
