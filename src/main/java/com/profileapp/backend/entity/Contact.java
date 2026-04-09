package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

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

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "nom", length = 100)
    private String nom;

    @Column(name = "telephone", length = 20)
    private String telephone;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 20)
    @Builder.Default
    private ContactStatus status = ContactStatus.PROSPECT;

    /* ═══ NOUVEAU — archivage soft-delete ═══
     * true  = contact visible dans la liste principale
     * false = contact archivé (bouton "Supprimer" → archive)
     * Pas de suppression physique → intégrité référentielle préservée.
     */
    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    @Column(name = "date_creation", nullable = false, updatable = false)
    private LocalDateTime dateCreation;

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

    @OneToMany(
        mappedBy = "contact",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Commande> commandes = new ArrayList<>();

    @OneToOne(
        mappedBy = "contact",
        fetch = FetchType.LAZY
    )
    private ConversionLog conversionLog;

    @PrePersist
    protected void onCreate() {
        this.dateCreation = LocalDateTime.now();
    }

    public void addCommande(Commande commande) {
        this.commandes.add(commande);
        commande.setContact(this);
    }

    public void removeCommande(Commande commande) {
        this.commandes.remove(commande);
        commande.setContact(null);
    }

    public void addConversionLog(ConversionLog log) {
        this.conversionLog = log;
        log.setContact(this);
    }
}
