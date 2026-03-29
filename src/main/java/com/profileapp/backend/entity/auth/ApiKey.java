package com.profileapp.backend.entity.auth;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "api_keys")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ApiKey {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Nom descriptif — ex: "Agent IA principal", "Agent test"
    // Permet à l'admin d'identifier chaque clé dans l'interface
    @Column(nullable = false, length = 100)
    private String nom;

    // La clé elle-même — UUID généré côté service
    // unique = true : deux agents ne peuvent pas avoir la même clé
    @Column(nullable = false, unique = true, length = 100)
    private String cleApi;

    // true = clé utilisable / false = clé révoquée
    // L'admin peut désactiver une clé sans la supprimer
    @Column(nullable = false)
    @Builder.Default
    private boolean actif = true;

    // Date de création — remplie automatiquement
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Date d'expiration optionnelle — null = pas d'expiration
    @Column
    private LocalDateTime expiresAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
