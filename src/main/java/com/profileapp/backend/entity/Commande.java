package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ Commande
 *
 * Représente un engagement commercial entre GNO et un Contact.
 * Peut être un devis en attente, un projet en cours, ou livré.
 *
 * Relations :
 *   → (N) Commandes appartiennent à (1) Contact       [ManyToOne - propriétaire]
 *   → (1) Commande a (N) LigneCommandes               [OneToMany - côté inverse]
 *
 * DÉCISION BigDecimal pour montant :
 * Double et Float sont INTERDITS pour les montants financiers
 * (problèmes d'arrondi). BigDecimal garantit la précision exacte.
 * precision=15 scale=2 → jusqu'à 9 999 999 999 999.99 FCFA
 */
@Entity
@Table(name = "commandes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Commande {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_commande", nullable = false, updatable = false)
    private LocalDateTime dateCommande;

    // ════════════════════════════════════════════════════════════
    // MONTANT — BigDecimal obligatoire pour les montants financiers
    // ════════════════════════════════════════════════════════════
    @Column(name = "montant", precision = 15, scale = 2)
    private BigDecimal montant;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private CommandeStatus status = CommandeStatus.DEVIS;

    @Column(name = "description_besoin", columnDefinition = "TEXT")
    private String descriptionBesoin;

    // ════════════════════════════════════════════════════════════
    // FICHE DEVIS URL — lien vers le fichier PDF du devis
    // On stocke uniquement l'URL, pas le fichier en base.
    // ════════════════════════════════════════════════════════════
    @Column(name = "fiche_devis_url", length = 500)
    private String ficheDevisUrl;

    @Column(name = "date_livraison_prevu")
    private LocalDate dateLivraisonPrevu;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → Contact
    //
    // Commande EST le côté propriétaire : elle porte la FK contact_id.
    // nullable = false : une Commande doit toujours appartenir
    // à un Contact. Pas de commande sans client/prospect.
    //
    // ON DELETE RESTRICT par défaut → la base refusera de supprimer
    // un Contact qui a encore des Commandes. Comportement voulu :
    // on ne doit pas perdre l'historique des commandes.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "contact_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_commande_contact")
    )
    private Contact contact;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → LigneCommande
    //
    // mappedBy = "commande" → LigneCommande porte la FK commande_id.
    //
    // cascade = {PERSIST, MERGE} → sauvegarder une Commande
    // avec ses LigneCommandes les sauvegarde toutes.
    //
    // orphanRemoval = true → si on retire une LigneCommande
    // de la liste, elle est automatiquement supprimée en base.
    // Pourquoi ici et pas ailleurs ? Une LigneCommande ne peut
    // pas exister sans sa Commande — c'est une "partie" de la Commande.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "commande",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        orphanRemoval = true,
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<LigneCommande> ligneCommandes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dateCommande = LocalDateTime.now();
    }

    // ════════════════════════════════════════════════════════════
    // HELPER METHODS
    // ════════════════════════════════════════════════════════════

    public void addLigneCommande(LigneCommande ligne) {
        this.ligneCommandes.add(ligne);
        ligne.setCommande(this);
    }

    public void removeLigneCommande(LigneCommande ligne) {
        this.ligneCommandes.remove(ligne);
        ligne.setCommande(null);
    }
}
