package com.profileapp.backend.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "token_blacklist")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TokenBlacklist {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Token JWT complet — TEXT car un JWT dépasse souvent 255 chars
    // unique = true : on ne peut pas blacklister deux fois le même token
    @Column(nullable = false, columnDefinition = "TEXT", unique = true)
    private String token;

    // Email de l'admin qui s'est déconnecté — pour les logs d'audit
    @Column(nullable = false)
    private String email;

    // Horodatage du logout — rempli automatiquement par @PrePersist
    @Column(nullable = false, updatable = false)
    private LocalDateTime logoutAt;

    // Date d'expiration naturelle du token
    // Utilisé pour nettoyer la table : DELETE WHERE expiresAt < NOW()
    // Inutile de garder un token expiré en blacklist — JwtUtil le refuserait de toute façon
    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.logoutAt = LocalDateTime.now();
    }
}
