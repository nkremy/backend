package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ SousService
 *
 * Représente une offre concrète et vendable au sein d'un Service (pôle).
 * Ex: Service "Développement" → SousService "Site vitrine", "E-commerce",
 *     "Application mobile", "Logiciel sur mesure"
 *
 * Un SousService peut avoir plusieurs images pour un scroll horizontal
 * dans le catalogue client.
 *
 * Relations :
 *   → (N) SousServices appartiennent à (1) Service     [ManyToOne - propriétaire]
 *   → (1) SousService a (N) SousServiceImages          [OneToMany - côté inverse]
 *   → (1) SousService a (N) LigneCommandes             [OneToMany - côté inverse]
 */
@Entity
@Table(name = "sous_services")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SousService {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "nom", nullable = false, length = 150)
    private String nom;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // ════════════════════════════════════════════════════════════
    // PRIX DE BASE — tarif de référence de ce sous-service
    // C'est ici le vrai prix de l'offre vendable.
    // Le prix réel négocié par client reste dans LigneCommande.
    // BigDecimal obligatoire pour les montants financiers.
    // ════════════════════════════════════════════════════════════
    @Column(name = "prix_base", precision = 15, scale = 2)
    private BigDecimal prixBase;

    // ════════════════════════════════════════════════════════════
    // ORDRE — position d'affichage dans le service parent
    // ════════════════════════════════════════════════════════════
    @Column(name = "ordre")
    @Builder.Default
    private Integer ordre = 0;

    // ════════════════════════════════════════════════════════════
    // ACTIF — permet de désactiver un sous-service sans le supprimer
    // Utile pour les offres saisonnières ou en pause.
    // ════════════════════════════════════════════════════════════
    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → Service
    //
    // SousService EST le côté propriétaire : il porte la FK service_id.
    // nullable = false : un SousService doit toujours appartenir
    // à un Service parent.
    // ON DELETE RESTRICT par défaut → on ne peut pas supprimer
    // un Service qui a encore des SousServices.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "service_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_sous_service_service")
    )
    private Service service;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → SousServiceImage
    //
    // cascade ALL + orphanRemoval = true :
    // Les images appartiennent entièrement à leur SousService.
    // Supprimer un SousService supprime toutes ses images.
    // Retirer une image de la liste la supprime en base.
    // orderBy ordre : images triées pour le scroll horizontal.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "sousService",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @OrderBy("ordre ASC")
    @Builder.Default
    private List<SousServiceImage> images = new ArrayList<>();

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → LigneCommande
    //
    // mappedBy = "sousService" → LigneCommande porte la FK.
    // Pas de cascade : garder l'historique des commandes.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "sousService",
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<LigneCommande> ligneCommandes = new ArrayList<>();

    // ════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ════════════════════════════════════════════════════════════

    public void addImage(SousServiceImage image) {
        this.images.add(image);
        image.setSousService(this);
    }

    public void removeImage(SousServiceImage image) {
        this.images.remove(image);
        image.setSousService(null);
    }
}
