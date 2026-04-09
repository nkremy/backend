package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Message;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    @EntityGraph(attributePaths = {"echange", "echange.contact", "agent", "analyse"})
    Optional<Message> findById(Long id);

    /*
     * Charge echange.contact pour avoir contactId/contactEmail
     * dans le chip "Voir contact" de MessagesPage sans requête supplémentaire.
     */
    @EntityGraph(attributePaths = {"echange", "echange.contact", "agent"})
    List<Message> findAll();

    boolean existsByThreadIdGmail(String threadIdGmail);
    boolean existsByMessageIdGmail(String messageIdGmail);
}
