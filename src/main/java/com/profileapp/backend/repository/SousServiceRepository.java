package com.profileapp.backend.repository;

import com.profileapp.backend.entity.SousService;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface SousServiceRepository extends JpaRepository<SousService, Long> {

    @EntityGraph(attributePaths = {"images", "service"})
    Optional<SousService> findById(Long id);

    @EntityGraph(attributePaths = {"images", "service"})
    List<SousService> findAllByOrderByOrdreAsc();

    @EntityGraph(attributePaths = {"images"})
    List<SousService> findByServiceIdOrderByOrdreAsc(Long serviceId);

    @EntityGraph(attributePaths = {"images"})
    List<SousService> findByServiceIdAndActifTrueOrderByOrdreAsc(Long serviceId);

    boolean existsByNomAndServiceId(String nom, Long serviceId);
    boolean existsByNomAndServiceIdAndIdNot(String nom, Long serviceId, Long id);
}
