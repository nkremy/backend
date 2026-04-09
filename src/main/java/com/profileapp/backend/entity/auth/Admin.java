package com.profileapp.backend.entity.auth;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "admins")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Identifiant de connexion — unique en base
    @Column(nullable = false, unique = true, length = 150)
    private String email;

    // Hash BCrypt du mot de passe — JAMAIS le mot de passe en clair
    // BCrypt produit toujours une chaîne de 60 caractères exactement
    @Column(nullable = false, length = 60)
    private String motDePasse;
}
