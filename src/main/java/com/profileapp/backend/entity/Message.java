package com.profileapp.backend.entity;

// import com.gno.crm.enums.Direction;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * ENTITÉ Message
 *
 * Représente un email individuel dans le fil de conversation.
 * Peut être entrant (du contact vers GNO) ou sortant (de GNO vers le contact).
 *
 * Relations :
 *   → (N) Messages appartiennent à (1) Echange     [ManyToOne - propriétaire]
 *   → (N) Messages peuvent être liés à (1) Agent   [ManyToOne - nullable]
 *   → (1) Message peut avoir (1) Analyse            [OneToOne - côté inverse]
 *
 * DÉCISION agent nullable :
 * agent = null  → le message a été traité/envoyé par un humain
 * agent != null → le message a été généré ou traité par l'agent IA
 * C'est toujours l'un OU l'autre, jamais les deux.
 */
@Entity
@Table(name = "messages")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ════════════════════════════════════════════════════════════
    // DATE ET DIRECTION
    //
    // dateHeure : horodatage exact du message.
    // Utilisé pour trier les messages dans l'Echange (@OrderBy).
    //
    // direction : ENTRANT = reçu de l'extérieur (contact → GNO)
    //             SORTANT = envoyé vers l'extérieur (GNO → contact)
    // Indispensable pour reconstruire visuellement la conversation.
    // ════════════════════════════════════════════════════════════
    @Column(name = "date_heure", nullable = false)
    private LocalDateTime dateHeure;

    @Enumerated(EnumType.STRING)
    @Column(name = "direction", nullable = false, length = 10)
    private Direction direction;

    @Column(name = "sujet_email", length = 255)
    private String sujetEmail;

    // ════════════════════════════════════════════════════════════
    // RESUME IA — résumé automatique généré par l'agent
    //
    // Stocké ici pour éviter de relire le message complet
    // à chaque affichage dans la liste des échanges.
    // Null si le message n'a pas encore été analysé.
    // ════════════════════════════════════════════════════════════
    @Column(name = "resume_ia", length = 500)
    private String resumeIa;

    // ════════════════════════════════════════════════════════════
    // MESSAGE COMPLET — corps brut de l'email
    //
    // columnDefinition = "TEXT" → contenu long sans limite de 255.
    // Nullable : on peut avoir le sujet sans le corps complet
    // si l'email n'a pas encore été entièrement récupéré via l'API Gmail.
    // ════════════════════════════════════════════════════════════
    @Column(name = "message_complet", columnDefinition = "TEXT")
    private String messageComplet;

    // ════════════════════════════════════════════════════════════
    // IDENTIFIANTS GMAIL — clés de traçabilité avec l'API Gmail
    //
    // messageIdGmail : identifiant unique du message dans Gmail.
    //   Sert à retrouver le message exact dans la boîte mail.
    //   unique = true : deux lignes ne peuvent pas pointer
    //   vers le même email Gmail.
    //
    // threadIdGmail : identifiant du fil de conversation Gmail.
    //   Plusieurs messages peuvent partager le même threadId
    //   (les réponses d'un même fil).
    //   Utile pour regrouper des messages liés côté Gmail.
    // ════════════════════════════════════════════════════════════
    @Column(name = "message_id_gmail", unique = true, length = 255)
    private String messageIdGmail;

    @Column(name = "thread_id_gmail", length = 255)
    private String threadIdGmail;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → Echange
    //
    // Message EST le côté propriétaire : il porte la FK echange_id.
    // nullable = false : un Message DOIT appartenir à un Echange.
    // fetch = LAZY : on ne charge pas tout l'Echange à chaque Message.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "echange_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_message_echange")
    )
    private Echange echange;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → Agent (NULLABLE)
    //
    // nullable = true → Si null, c'est un humain qui a traité
    // ce message. Si non-null, c'est l'agent IA.
    // C'est toujours l'un OU l'autre — jamais les deux.
    //
    // fetch = LAZY : on ne charge pas l'Agent à chaque Message.
    // Pas de cascade : l'Agent existe indépendamment des Messages.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "agent_id",
        nullable = true,
        foreignKey = @ForeignKey(name = "fk_message_agent")
    )
    private Agent agent;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToOne → Analyse (côté INVERSE)
    //
    // mappedBy = "message" → Analyse porte la FK message_id.
    // Un Message peut avoir au maximum une Analyse associée.
    // Nullable implicitement : tous les messages n'ont pas
    // forcément une Analyse (ex: message sortant simple).
    // fetch = LAZY : on ne charge pas l'Analyse à chaque Message.
    // ════════════════════════════════════════════════════════════
    @OneToOne(
        mappedBy = "message",
        fetch = FetchType.LAZY
    )
    private Analyse analyse;
}
