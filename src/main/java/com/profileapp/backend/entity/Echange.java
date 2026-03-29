package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * ENTITÉ Echange
 *
 * Représente le fil de conversation unique entre GNO et un Contact.
 * C'est le "thread global" qui regroupe tous les Messages échangés.
 *
 * Relations :
 *   → (1) Echange est référencé par (1) Contact   [OneToOne - côté inverse]
 *   → (1) Echange a (N) Messages                  [OneToMany - côté inverse]
 *
 * DÉCISION IMPORTANTE : Echange est le côté INVERSE de la relation
 * OneToOne avec Contact. C'est Contact qui porte la FK echange_id.
 * Ici on met mappedBy = "echange" pour le refléter.
 *
 * Pas de @PrePersist ici : Echange n'a pas de date propre,
 * les dates sont portées par chaque Message individuel.
 */
@Entity
@Table(name = "echanges")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Echange {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToOne → Contact (côté INVERSE)
    //
    // mappedBy = "echange" → Contact porte la FK.
    // Echange ne gère pas la clé étrangère.
    //
    // fetch = LAZY → on ne charge pas le Contact à chaque
    // chargement de l'Echange.
    // ════════════════════════════════════════════════════════════
    @OneToOne(
        mappedBy = "echange",
        fetch = FetchType.LAZY
    )
    private Contact contact;

    // ════════════════════════════════════════════════════════════
    // RELATION OneToMany → Message
    //
    // mappedBy = "echange" → Message porte la FK echange_id.
    //
    // cascade = {PERSIST, MERGE} → sauvegarder un Echange
    // avec de nouveaux Messages les sauvegarde aussi.
    //
    // orderBy dateHeure → les messages sont toujours retournés
    // dans l'ordre chronologique. Indispensable pour afficher
    // un historique cohérent.
    // ════════════════════════════════════════════════════════════
    @OneToMany(
        mappedBy = "echange",
        cascade = {CascadeType.PERSIST, CascadeType.MERGE},
        fetch = FetchType.LAZY
    )
    @OrderBy("dateHeure ASC")
    @Builder.Default
    private List<Message> messages = new ArrayList<>();

    // ════════════════════════════════════════════════════════════
    // HELPER METHOD
    // ════════════════════════════════════════════════════════════

    public void addMessage(Message message) {
        this.messages.add(message);
        message.setEchange(this);
    }

    public void removeMessage(Message message) {
        this.messages.remove(message);
        message.setEchange(null);
    }
}
