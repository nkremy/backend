package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * ENTITÉ LigneCommande
 *
 * Relie une Commande à un Service (et optionnellement un SousService)
 * avec les détails spécifiques négociés pour ce client précis.
 *
 * Relations :
 *   → (N) LigneCommandes appartiennent à (1) Commande    [ManyToOne - propriétaire]
 *   → (N) LigneCommandes référencent    (1) Service      [ManyToOne - propriétaire]
 *   → (N) LigneCommandes référencent    (0/1) SousService [ManyToOne - optionnel]
 *
 * DÉCISION sous_service_id nullable :
 * Les commandes existantes référencent un Service global.
 * Les nouvelles commandes peuvent aussi cibler un SousService précis.
 * La rétrocompatibilité est garantie : sous_service_id peut être null.
 */
@Entity
@Table(name = "ligne_commandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LigneCommande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ════════════════════════════════════════════════════════════
    // PRIX UNITAIRE — le prix réel négocié pour ce client
    // Différent du Service.prixBase qui est indicatif.
    // BigDecimal obligatoire.
    // ════════════════════════════════════════════════════════════
    @Column(name = "prix_unitaire", nullable = false, precision = 15, scale = 2)
    private BigDecimal prixUnitaire;

    // ════════════════════════════════════════════════════════════
    // DÉTAILS SPÉCIFIQUES — les specs propres à ce client
    // Ce champ différencie deux commandes du même service.
    // ════════════════════════════════════════════════════════════
    @Column(name = "details_specifiques", columnDefinition = "TEXT")
    private String detailsSpecifiques;

    @Column(name = "duree_estimation", length = 100)
    private String dureeEstimation;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → Commande
    //
    // LigneCommande porte la FK commande_id.
    // nullable = false : une LigneCommande DOIT appartenir
    // à une Commande. Elle ne peut pas exister seule.
    // (cohérent avec orphanRemoval = true dans Commande)
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "commande_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_ligne_commande")
    )
    private Commande commande;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → Service
    //
    // LigneCommande porte la FK service_id.
    // nullable = false : chaque ligne doit référencer un service pôle.
    // ON DELETE RESTRICT → on ne peut pas supprimer un Service
    // référencé dans une LigneCommande existante.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "service_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_ligne_service")
    )
    private Service service;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → SousService (OPTIONNEL)
    //
    // nullable = true : rétrocompatibilité avec les commandes
    // existantes qui ne référencent qu'un Service global.
    // Les nouvelles commandes peuvent cibler un SousService précis.
    // ON DELETE RESTRICT → on ne peut pas supprimer un SousService
    // référencé dans une LigneCommande existante.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "sous_service_id",
        nullable = true,
        foreignKey = @ForeignKey(name = "fk_ligne_sous_service")
    )
    private SousService sousService;
}
