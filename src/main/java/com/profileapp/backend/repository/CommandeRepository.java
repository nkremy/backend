package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Commande;
import com.profileapp.backend.entity.CommandeStatus;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CommandeRepository extends JpaRepository<Commande, Long> {

    @EntityGraph(attributePaths = {"contact", "ligneCommandes", "ligneCommandes.service"})
    Optional<Commande> findById(Long id);

    @EntityGraph(attributePaths = {"contact"})
    List<Commande> findAll();

    @EntityGraph(attributePaths = {"ligneCommandes", "ligneCommandes.service"})
    List<Commande> findAllByContactId(Long contactId);

    @EntityGraph(attributePaths = {"contact"})
    List<Commande> findAllByStatus(CommandeStatus status);
}
