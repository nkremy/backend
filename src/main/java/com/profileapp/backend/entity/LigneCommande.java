package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

/**
 * ENTITÉ LigneCommande
 *
 * Relie une Commande à un Service avec les détails spécifiques
 * négociés pour ce client précis.
 *
 * Relations :
 *   → (N) LigneCommandes appartiennent à (1) Commande  [ManyToOne - propriétaire]
 *   → (N) LigneCommandes référencent (1) Service       [ManyToOne - propriétaire]
 *
 * DÉCISION : LigneCommande porte les deux FK (commande_id, service_id).
 * C'est elle qui fait le lien entre les deux.
 * Elle enrichit ce lien avec le prix réel et les détails spécifiques.
 * Ex: Service "Site web" + prixUnitaire = 350 000 FCFA
 *   + détails = "5 pages, formulaire contact, responsive mobile"
 *   + duree = "3 semaines"
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
    // nullable = false : chaque ligne doit référencer un service.
    //
    // ON DELETE RESTRICT par défaut → la base refusera de supprimer
    // un Service référencé dans une LigneCommande existante.
    // C'est le comportement voulu : on ne peut pas supprimer
    // un service qui a été vendu.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "service_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_ligne_service")
    )
    private Service service;
}
