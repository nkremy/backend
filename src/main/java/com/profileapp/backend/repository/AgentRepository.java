package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Agent;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AgentRepository extends JpaRepository<Agent, Long> {

    @EntityGraph(attributePaths = {"messages", "analyses"})
    Optional<Agent> findById(Long id);

    List<Agent> findAll();

    Optional<Agent> findFirstByActifTrue();
}
