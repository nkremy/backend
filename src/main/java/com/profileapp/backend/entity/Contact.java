package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ Contact
 *
 * Centre du CRM. Représente un prospect ou un client.
 *
 * Relations :
 *   → (1) Contact a (1) Echange          [OneToOne - côté propriétaire]
 *   → (1) Contact a (N) Commandes        [OneToMany - côté inverse]
 *   → (1) Contact a (N) ConversionLogs   [OneToMany - côté inverse]
 *
 * DÉCISION : Contact est le côté propriétaire de la relation
 * OneToOne avec Echange. C'est Contact qui porte la FK echange_id.
 * Pourquoi ? Un Contact CRÉE son Echange — il est responsable
 * de cette relation. Sans Contact, pas d'Echange.
 */
@Entity
@Table(name = "contacts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ════════════════════════════════════════════════════════════
    // EMAIL — identifiant métier unique
    // C'est sur l'email que l'agent identifie un contact existant.
    // nullable = false : on ne crée jamais un contact sans email.
    // ════════════════════════════════════════════════════════════
    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "nom", length = 100)
    private String nom;

    @Column(name = "telephone", length = 20)
    private String telephone;

    // ════════════════════════════════════════════════════════════
    // STATUT — valeur contrôlée via enum → stockée en String en base
    //
    // @Enumerated(STRING) → stocke "PROSPECT" / "CLIENT" en base.
    // Jamais EnumType.ORDINAL : si l'ordre change dans l'enum,
    // toutes les données existantes deviennent incorrectes.
    //
    // nullable = false : un contact EST toujours prospect ou client.
    // Default = PROSPECT : tout nouveau contact commence prospect.
    // ════════════════════════════════════════════════════════════
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ContactStatus status = ContactStatus.PROSPECT;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToOne → Echange
    //
    // Contact EST le côté propriétaire : il porte la FK echange_id.
    //
    // cascade = {PERSIST, MERGE} → quand on crée/modifie un Contact
    // avec son Echange, les deux sont sauvegardés ensemble.
    // Pas de REMOVE : supprimer un Contact ne supprime pas
    // l'Echange automatiquement — on gère ça explicitement
    // dans le service pour garder le contrôle.
    //
    // fetch = LAZY → on ne charge pas l'Echange à chaque
    // chargement de Contact. On l'accède uniquement si besoin.
    //
    // @JoinColumn → Contact porte la FK "echange_id" en base.
    // nullable = true car l'Echange est créé après le Contact
    // (un nouveau Contact n'a pas encore d'échanges).
    // ════════════════════════════════════════════════════════════
    @OneToOne(
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY
    )
    @JoinColumn(
        name = "echange_id",
        nullable = true,
        foreignKey = @ForeignKey(name = "fk_contact_echange")
    )
    private Echange echange;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → Commande
    //
    // mappedBy = "contact" → Commande porte la FK contact_id.
    // Contact est le côté INVERSE ici.
    //
    // cascade = {PERSIST, MERGE} → sauvegarder le Contact
    // sauvegarde aussi ses Commandes liées si elles sont nouvelles.
    // Pas de REMOVE : supprimer un Contact ne supprime pas
    // ses Commandes en cascade — décision métier explicite.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "contact",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Commande> commandes = new ArrayList<>();

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → ConversionLog
    //
    // Historique de toutes les conversions de ce contact.
    // Pas de cascade : les logs sont créés explicitement
    // dans le service de conversion — jamais en cascade.
    // ════════════════════════════════════════════════════════════
    @OneToOne(
        mappedBy = "contact",
        fetch = FetchType.LAZY
    )
    private ConversionLog conversionLog ;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    // ════════════════════════════════════════════════════════════
    // HELPER METHODS — maintiennent la cohérence bidirectionnelle
    // ════════════════════════════════════════════════════════════

    public void addCommande(Commande commande) {
        this.commandes.add(commande);
        commande.setContact(this);
    }

    public void removeCommande(Commande commande) {
        this.commandes.remove(commande);
        commande.setContact(null);
    }

    public void addConversionLog(ConversionLog log) {
        this.conversionLog = log ;
        log.setContact(this);
    }
}
