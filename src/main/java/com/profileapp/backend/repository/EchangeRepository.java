package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Echange;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface EchangeRepository extends JpaRepository<Echange, Long> {

    @EntityGraph(attributePaths = {"contact", "messages", "messages.agent", "messages.analyse"})
    Optional<Echange> findById(Long id);

    @EntityGraph(attributePaths = {"messages", "messages.agent", "messages.analyse"})
    Optional<Echange> findByContactId(Long contactId);
}
