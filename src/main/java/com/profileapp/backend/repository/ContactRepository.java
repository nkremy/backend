package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Contact;
import com.profileapp.backend.entity.ContactStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    @EntityGraph(attributePaths = { "echange", "echange.messages", "commandes", "conversionLog" })
    Optional<Contact> findById(Long id);

    /* Contacts actifs seulement (liste principale) */
    @EntityGraph(attributePaths = { "echange", "commandes", "conversionLog" })
    List<Contact> findAllByActifTrue();

    /* Contacts actifs filtrés par statut */
    @EntityGraph(attributePaths = { "echange", "commandes", "conversionLog" })
    List<Contact> findAllByActifTrueAndStatus(ContactStatus status);

    /* Contacts archivés */
    @EntityGraph(attributePaths = { "echange", "commandes", "conversionLog" })
    List<Contact> findAllByActifFalse();

    // @EntityGraph(attributePaths = { "echange.messages", "commandes", "conversionLog" })
    Optional<Contact> findByEmail(String email);

    /*
     * Tous les contacts actifs — pour la liste déroulante dans le formulaire
     * message
     */
    List<Contact> findAllByActifTrueOrderByNomAsc();

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
