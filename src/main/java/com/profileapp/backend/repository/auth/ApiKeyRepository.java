package com.profileapp.backend.repository.auth;

import com.profileapp.backend.entity.auth.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, Long> {

    // Recherche par valeur de clé — utilisé dans ApiKeyFilter pour valider chaque requête agent
    Optional<ApiKey> findByCleApi(String cleApi);

    // Liste toutes les clés actives — utilisé dans AdminController pour l'affichage
    List<ApiKey> findAllByActifTrue();
}
