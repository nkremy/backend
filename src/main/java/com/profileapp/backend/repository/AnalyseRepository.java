package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Analyse;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AnalyseRepository extends JpaRepository<Analyse, Long> {

    @EntityGraph(attributePaths = {"message", "agent"})
    Optional<Analyse> findById(Long id);

    @EntityGraph(attributePaths = {"message", "agent"})
    List<Analyse> findAll();

    boolean existsByMessageId(Long messageId);
}
