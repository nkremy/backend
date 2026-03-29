package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ Service
 *
 * Catalogue des services proposés par GNO SOLUTIONS.
 * Ex: Site web vitrine, Application mobile, Community management...
 *
 * Relations :
 *   → (1) Service a (N) LigneCommandes  [OneToMany - côté inverse]
 *
 * DÉCISION : Service est le catalogue de référence.
 * Le prix réel négocié par client est dans LigneCommande.prixUnitaire.
 * prixBase ici est juste le tarif indicatif de départ.
 */
@Entity
@Table(name = "services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Service {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, unique = true, length = 150)
    private String nom;

    // ════════════════════════════════════════════════════════════
    // PRIX DE BASE — tarif indicatif de référence
    // Le prix réel facturé au client est dans LigneCommande.
    // BigDecimal obligatoire pour les montants financiers.
    // ════════════════════════════════════════════════════════════
    @Column(name = "prix_base", precision = 15, scale = 2)
    private BigDecimal prixBase;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → LigneCommande
    //
    // mappedBy = "service" → LigneCommande porte la FK service_id.
    // Pas de cascade : supprimer un Service ne supprime pas
    // les LigneCommandes historiques — on garde la traçabilité.
    // ON DELETE RESTRICT par défaut → la base bloquera la suppression
    // d'un Service qui a des LigneCommandes existantes.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "service",
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<LigneCommande> ligneCommandes = new ArrayList<>();
}
