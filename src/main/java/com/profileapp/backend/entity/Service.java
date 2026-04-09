package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ Service
 *
 * Catalogue des services (pôles) proposés par GNO SOLUTIONS.
 * Ex: Développement, Sécurité & Réseaux, Marketing Digital...
 *
 * Un Service est un pôle global. Chaque pôle contient N sous-services
 * qui sont les vraies offres vendables au client.
 *
 * Relations :
 *   → (1) Service a (N) SousServices    [OneToMany - côté inverse]
 *   → (1) Service a (N) LigneCommandes  [OneToMany - côté inverse]
 *
 * CHAMPS AJOUTÉS :
 *   - imageUrl  : image de couverture du pôle (URL locale ou externe)
 *   - ordre     : tri d'affichage dans le catalogue
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
    // PRIX DE BASE — tarif indicatif d'entrée de gamme du pôle
    // Le prix réel facturé au client est dans LigneCommande.
    // BigDecimal obligatoire pour les montants financiers.
    // ════════════════════════════════════════════════════════════
    @Column(name = "prix_base", precision = 15, scale = 2)
    private BigDecimal prixBase;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ════════════════════════════════════════════════════════════
    // IMAGE — URL vers l'image de couverture du service
    // Stockage local : ex. "uploads/photos/services/dev.png"
    // On ne stocke QUE l'URL, jamais le fichier en base.
    // ════════════════════════════════════════════════════════════
    @Column(name = "image_url", length = 500)
    private String imageUrl;

    // ════════════════════════════════════════════════════════════
    // ORDRE — position d'affichage dans le catalogue
    // Permet de trier les services sans modifier les IDs.
    // ════════════════════════════════════════════════════════════
    @Column(name = "ordre")
    @Builder.Default
    private Integer ordre = 0;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → SousService
    //
    // mappedBy = "service" → SousService porte la FK service_id.
    // cascade PERSIST/MERGE : sauvegarder un Service avec ses
    // sous-services les sauvegarde tous.
    // orphanRemoval = true : retirer un SousService de la liste
    // le supprime en base. Un SousService ne peut exister sans
    // son Service parent.
    // orderBy ordre : les sous-services sont triés par ordre.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "service",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("ordre ASC")
    @Builder.Default
    private List<SousService> sousServices = new ArrayList<>();

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

    // ════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ════════════════════════════════════════════════════════════

    public void addSousService(SousService ss) {
        this.sousServices.add(ss);
        ss.setService(this);
    }

    public void removeSousService(SousService ss) {
        this.sousServices.remove(ss);
        ss.setService(null);
    }
}
