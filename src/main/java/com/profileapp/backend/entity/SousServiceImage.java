package com.profileapp.backend.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * ENTITÉ SousServiceImage
 *
 * Représente une image associée à un SousService.
 * Un SousService peut avoir N images → scroll horizontal dans le catalogue.
 *
 * DÉCISION : On stocke uniquement l'URL de l'image, jamais le fichier.
 * En local : "uploads/photos/sous-services/vitrine-1.png"
 * En production : URL CDN / Cloudinary / S3
 * Même pattern que Commande.ficheDevisUrl.
 *
 * Relations :
 *   → (N) SousServiceImages appartiennent à (1) SousService [ManyToOne - propriétaire]
 */
@Entity
@Table(name = "sous_service_images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SousServiceImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ════════════════════════════════════════════════════════════
    // IMAGE URL — chemin vers l'image
    // Local  : "uploads/photos/sous-services/nom-fichier.png"
    // Distant: "https://cdn.exemple.com/image.jpg"
    // On ne stocke jamais le binaire en base.
    // ════════════════════════════════════════════════════════════
    @Column(name = "image_url", nullable = false, length = 500)
    private String imageUrl;

    // ════════════════════════════════════════════════════════════
    // ORDRE — détermine la position dans le scroll horizontal
    // Image ordre=0 → première affichée, ordre=1 → deuxième, etc.
    // ════════════════════════════════════════════════════════════
    @Column(name = "ordre")
    @Builder.Default
    private Integer ordre = 0;

    // ════════════════════════════════════════════════════════════
    // RELATION ManyToOne → SousService
    //
    // SousServiceImage porte la FK sous_service_id.
    // nullable = false : une image sans SousService n'a pas de sens.
    // ON DELETE CASCADE (géré par orphanRemoval dans SousService)
    // → supprimer un SousService supprime toutes ses images.
    // ════════════════════════════════════════════════════════════
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "sous_service_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "fk_image_sous_service")
    )
    private SousService sousService;
}
