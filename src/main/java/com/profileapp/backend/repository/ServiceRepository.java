package com.profileapp.backend.repository;

import com.profileapp.backend.entity.Service;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ServiceRepository extends JpaRepository<Service, Long> {

    /* findById — on charge tout car c'est un seul service
       Pas de MultipleBagFetchException car Hibernate gère 1 résultat */
    @EntityGraph(attributePaths = {"sousServices", "sousServices.images", "ligneCommandes"})
    Optional<Service> findById(Long id);

    /* findAll — PAS d'@EntityGraph pour éviter MultipleBagFetchException
       On charge sousServices via JOIN FETCH (1 bag max)
       Les images seront chargées en lazy par le service */
    @Query("SELECT DISTINCT s FROM Service s LEFT JOIN FETCH s.sousServices ORDER BY s.ordre ASC")
    List<Service> findAllWithSousServices();

    /* Version simple sans fetch pour les cas légers */
    List<Service> findAllByOrderByOrdreAsc();

    boolean existsByNom(String nom);
    boolean existsByNomAndIdNot(String nom, Long id);
}
