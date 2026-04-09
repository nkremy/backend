package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ Agent
 *
 * Représente un modèle d'IA configuré dans le système.
 * Ex: Claude Sonnet 3.7, GPT-4o, LLaMA 3.
 *
 * Relations :
 *   → (1) Agent a (N) Messages traités   [OneToMany - côté inverse]
 *   → (1) Agent a (N) Analyses           [OneToMany - côté inverse]
 *
 * DÉCISION : On stocke l'Agent comme entité et pas comme simple
 * champ String, pour pouvoir changer de modèle et garder
 * l'historique de quel modèle a traité quel message.
 * C'est ce qui permet de comparer les performances entre modèles.
 */
@Entity
@Table(name = "agents")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Agent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ════════════════════════════════════════════════════════════
    // MODÈLE — identifiant technique du LLM
    // Ex: "claude-sonnet-4-6", "gpt-4o", "llama-3-70b"
    // ════════════════════════════════════════════════════════════
    @Column(name = "model", nullable = false, length = 100)
    private String model;

    @Column(name = "date_activation", nullable = false, updatable = false)
    private LocalDateTime dateActivation;

    @Column(name = "actif", nullable = false)
    @Builder.Default
    private Boolean actif = true;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → Message
    //
    // mappedBy = "agent" → Message porte la FK agent_id.
    // Pas de cascade : les Messages existent indépendamment.
    // On ne supprime pas les Messages si on désactive un Agent.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "agent",
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → Analyse
    //
    // Même logique : pas de cascade.
    // Les Analyses sont des logs — on ne les détruit jamais.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "agent",
        fetch = FetchType.LAZY
    )
    @Builder.Default
    private List<Analyse> analyses = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.dateActivation = LocalDateTime.now();
    }
}
