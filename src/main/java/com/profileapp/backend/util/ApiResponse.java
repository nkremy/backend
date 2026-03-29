
package com.profileapp.backend.util;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

/**
 * WRAPPER UNIVERSEL DE RÉPONSE — ApiResponse<T>
 *
 * Cette classe générique enveloppe TOUTES les réponses de l'API.
 * Le type générique <T> permet de l'utiliser avec n'importe quel
 * type de données :
 *   - ApiResponse<StudentResponseDTO>    → un seul étudiant
 *   - ApiResponse<List<StudentResponseDTO>> → liste d'étudiants
 *   - ApiResponse<Void>                  → pas de données (ex: DELETE)
 *
 * AVANTAGE : Le client a toujours le même contrat de réponse.
 * Il vérifie toujours "success" en premier, puis lit "data" ou "errors".
 *
 * @JsonInclude(JsonInclude.Include.NON_NULL) :
 * Les champs null ne seront PAS inclus dans le JSON final.
 * Ex: si "errors" est null, il n'apparaîtra pas dans la réponse JSON.
 * Cela allège la réponse et évite la confusion côté client.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    // ================================================================
    // INDICATEUR DE SUCCÈS
    // Premier champ que le client vérifie.
    // true  → opération réussie
    // false → une erreur s'est produite
    // ================================================================
    private boolean success;

    // ================================================================
    // MESSAGE LISIBLE PAR L'HUMAIN
    // Décrit ce qui s'est passé en langage naturel.
    // Ex: "Étudiant créé avec succès"
    //     "Étudiant non trouvé avec l'id : 5"
    //     "Erreur de validation des données"
    // ================================================================
    private String message;

    // ================================================================
    // DONNÉES DE LA RÉPONSE (générique)
    // Présent uniquement en cas de succès.
    // null en cas d'erreur (et grâce à @JsonInclude, absent du JSON)
    // Le type <T> s'adapte selon le contexte :
    //   T = StudentResponseDTO pour un seul étudiant
    //   T = List<StudentResponseDTO> pour une liste
    //   T = Void pour les opérations sans retour de données
    // ================================================================
    private T data;

    // ================================================================
    // LISTE DES ERREURS
    // Présente uniquement en cas d'erreur de validation.
    // Contient tous les messages d'erreur des champs invalides.
    // Ex: ["Le prénom est obligatoire", "Email invalide"]
    // null en cas de succès (absent du JSON grâce à @JsonInclude)
    // ================================================================
    private List<String> errors;

    // ================================================================
    // MÉTHODES FACTORY STATIQUES
    //
    // Pourquoi des méthodes factory ?
    // Au lieu d'écrire partout :
    //   ApiResponse.<StudentResponseDTO>builder()
    //       .success(true)
    //       .message("...")
    //       .data(dto)
    //       .build();
    //
    // On écrit simplement :
    //   ApiResponse.success("Étudiant créé", dto);
    //
    // C'est plus court, plus lisible, et évite les erreurs.
    // ================================================================

    /**
     * FACTORY : Réponse de succès AVEC données
     *
     * Utilisé pour : GET (un ou plusieurs), POST, PUT
     * Exemple : ApiResponse.success("Étudiant récupéré", studentDTO)
     *
     * @param message Message de succès
     * @param data    Les données à retourner
     * @param <T>     Type des données
     * @return ApiResponse avec success=true, message et data
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * FACTORY : Réponse de succès SANS données
     *
     * Utilisé pour : DELETE (204 No Content — pas de body nécessaire)
     * Mais on peut aussi l'utiliser pour confirmer une action
     * sans retourner de données.
     * Exemple : ApiResponse.success("Étudiant supprimé avec succès")
     *
     * @param message Message de succès
     * @param <T>     Type générique (Void en général)
     * @return ApiResponse avec success=true et message seulement
     */
    public static <T> ApiResponse<T> success(String message) {
        return ApiResponse.<T>builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * FACTORY : Réponse d'erreur SIMPLE (sans liste d'erreurs)
     *
     * Utilisé pour : 404 Not Found, 409 Conflict, 500 Server Error
     * Exemple : ApiResponse.error("Étudiant non trouvé avec l'id : 5")
     *
     * @param message Message d'erreur descriptif
     * @param <T>     Type générique
     * @return ApiResponse avec success=false et message d'erreur
     */
    public static <T> ApiResponse<T> error(String message) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .build();
    }

    /**
     * FACTORY : Réponse d'erreur AVEC liste de détails
     *
     * Utilisé pour : 400 Bad Request (erreurs de validation)
     * Exemple : ApiResponse.error("Erreur de validation", listErreurs)
     *
     * @param message Message d'erreur général
     * @param errors  Liste détaillée des erreurs (un message par champ)
     * @param <T>     Type générique
     * @return ApiResponse avec success=false, message et liste d'erreurs
     */
    public static <T> ApiResponse<T> error(String message, List<String> errors) {
        return ApiResponse.<T>builder()
                .success(false)
                .message(message)
                .errors(errors)
                .build();
    }
}