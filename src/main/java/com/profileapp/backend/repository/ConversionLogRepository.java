package com.profileapp.backend.repository;

import com.profileapp.backend.entity.ConversionLog;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ConversionLogRepository extends JpaRepository<ConversionLog, Long> {

    @EntityGraph(attributePaths = {"contact"})
    Optional<ConversionLog> findById(Long id);

    @EntityGraph(attributePaths = {"contact"})
    List<ConversionLog> findAll();

    Optional<ConversionLog> findByContactId(Long contactId);

    boolean existsByContactId(Long contactId);
}
