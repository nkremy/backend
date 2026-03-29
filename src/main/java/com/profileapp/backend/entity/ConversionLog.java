package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ENTITÉ ConversionLog
 *
 * Journal d'audit des conversions prospect → client.
 * Chaque fois qu'un Contact change de statut, un log est créé.
 *
 * Relations :
 *   → (N) ConversionLogs appartiennent à (1) Contact  [ManyToOne - propriétaire]
 *
 * DÉCISION : Ce n'est pas un simple champ dateConversion sur Contact.
 * C'est une entité séparée car :
 *   1. On garde l'historique complet (un contact pourrait théoriquement
 *      revenir en prospect puis redevenir client).
 *   2. On sait QUI a déclenché la conversion (humain ou agent).
 *   3. On sait POURQUOI (motif).
 * Sans cette table, on ne peut répondre à aucune de ces questions.
 */
@Entity
@Table(name = "conversion_logs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConversionLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "date_conversion", nullable = false, updatable = false)
    private LocalDateTime dateConversion;

    // ════════════════════════════════════════════════════════════
    // MOTIF — contexte de la conversion
    // Ex: "Devis signé le 15/03/2025 — paiement reçu"
    //     "Commande #42 validée par le commercial"
    // ════════════════════════════════════════════════════════════
    @Column(name = "motif", columnDefinition = "TEXT")
    private String motif;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → Contact
    //
    // ConversionLog porte la FK contact_id.
    // nullable = false : un log de conversion DOIT être lié
    // à un Contact.
    // fetch = LAZY.
    // ════════════════════════════════════════════════════════════
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "contact_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_conversion_contact")
    )
    private Contact contact;

    @PrePersist
    protected void onCreate() {
        this.dateConversion = LocalDateTime.now();
    }
}
