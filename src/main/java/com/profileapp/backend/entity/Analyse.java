package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ENTITÉ Analyse
 *
 * Trace l'action que l'agent a effectuée face à un Message reçu.
 * C'est le journal d'audit de l'agent — chaque décision est enregistrée.
 *
 * Relations :
 *   → (1) Analyse est liée à (1) Message  [OneToOne - propriétaire]
 *   → (N) Analyses appartiennent à (1) Agent [ManyToOne - propriétaire]
 *
 * DÉCISION : Analyse est le côté propriétaire de la relation
 * OneToOne avec Message. Elle porte la FK message_id.
 * Pourquoi ? L'Analyse est créée APRÈS le Message, en réponse à lui.
 * C'est l'Analyse qui "connaît" son Message, pas l'inverse.
 */
@Entity
@Table(name = "analyses")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Analyse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ════════════════════════════════════════════════════════════
    // DESCRIPTION — ce que l'agent a compris et décidé
    //
    // Texte libre décrivant l'analyse effectuée :
    // "Email de demande de devis détecté. Contact nouveau créé.
    //  Accusé de réception envoyé automatiquement."
    // columnDefinition = "TEXT" → pas de limite de longueur.
    // ════════════════════════════════════════════════════════════
    @Column(name = "description", columnDefinition = "TEXT", nullable = false)
    private String description;

    @Column(name = "date_analyse", nullable = false, updatable = false)
    private LocalDateTime dateAnalyse;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToOne → Message (côté PROPRIÉTAIRE)
    //
    // Analyse porte la FK message_id.
    // nullable = false : une Analyse est toujours liée à un Message.
    // fetch = LAZY : on ne charge pas le Message à chaque Analyse.
    //
    // Pas de cascade : le Message existe avant l'Analyse
    // et indépendamment d'elle.
    // ════════════════════════════════════════════════════════════
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "message_id",
        nullable = false,
        unique = true,
        foreignKey = @ForeignKey(name = "fk_analyse_message")
    )
    private Message message;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → Agent
    //
    // Analyse porte la FK agent_id.
    // nullable = false : on sait toujours quel agent a produit
    // cette analyse.
    // fetch = LAZY.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "agent_id",
        nullable = true,
        foreignKey = @ForeignKey(name = "fk_analyse_agent")
    )
    private Agent agent;

    @PrePersist
    protected void onCreate() {
        this.dateAnalyse = LocalDateTime.now();
    }
}
