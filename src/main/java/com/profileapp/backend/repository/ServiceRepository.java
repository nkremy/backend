package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Service;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    @EntityGraph(attributePaths = {"ligneCommandes"})
    Optional<Service> findById(Long id);

    List<Service> findAll();

    boolean existsByNom(String nom);

    boolean existsByNomAndIdNot(String nom, Long id);
}
