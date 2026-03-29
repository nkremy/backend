package com.profileapp.backend.repository.auth;

import com.profileapp.backend.entity.auth.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {

    // Vérifie si un token est blacklisté — appelé dans JwtFilter à chaque requête protégée
    // existsBy = SELECT COUNT > 0, plus léger que findBy car on n'a pas besoin des données
    boolean existsByToken(String token);

    // Nettoyage des tokens expirés — inutile de les garder, JwtUtil les refuserait de toute façon
    // @Modifying : indique à Spring que c'est une écriture (DELETE), pas une lecture
    // @Transactional : obligatoire pour les opérations d'écriture JPQL
    @Modifying
    @Transactional
    @Query("DELETE FROM TokenBlacklist t WHERE t.expiresAt < :now")
    void deleteExpiredTokens(LocalDateTime now);
}
