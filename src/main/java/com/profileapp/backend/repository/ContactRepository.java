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

    @EntityGraph(attributePaths = {"echange", "echange.messages", "commandes", "conversionLog"})
    Optional<Contact> findById(Long id);

    @EntityGraph(attributePaths = {"echange", "commandes", "conversionLog"})
    List<Contact> findAll();

    @EntityGraph(attributePaths = {"echange", "echange.messages", "commandes", "conversionLog"})
    Optional<Contact> findByEmail(String email);

    @EntityGraph(attributePaths = {"echange", "commandes", "conversionLog"})
    List<Contact> findAllByStatus(ContactStatus status);

    boolean existsByEmail(String email);

    boolean existsByEmailAndIdNot(String email, Long id);
}
