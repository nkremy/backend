package com.profileapp.backend.exception;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.profileapp.backend.util.ApiResponse;

import java.util.List;
import java.util.stream.Collectors;

/**
 * GESTIONNAIRE GLOBAL DES EXCEPTIONS — GlobalExceptionHandler
 *
 * COMMENT SPRING FAIT LE LIEN ?
 * ─────────────────────────────
 * 1. @RestControllerAdvice → Spring enregistre cette classe comme
 *    "intercepteur global". Elle surveille TOUS les controllers.
 *
 * 2. @ExceptionHandler(XxxException.class) → Spring crée une table
 *    de routage interne :
 *    ResourceNotFoundException      → handleResourceNotFound()
 *    DuplicateResourceException     → handleDuplicateResource()
 *    MethodArgumentNotValidException → handleValidationErrors()
 *    Exception                      → handleGenericException()
 *
 * 3. QUAND UNE EXCEPTION EST LANCÉE dans le Service :
 *    → Spring interrompt l'exécution du Controller immédiatement
 *    → Spring remonte la pile d'appels (stack unwinding)
 *    → Spring cherche dans sa table : "qui gère cette exception ?"
 *    → Spring appelle la méthode du handler correspondant
 *    → C'est CETTE méthode qui produit la réponse HTTP finale
 *    → Le Controller ne retourne JAMAIS sa propre réponse
 *
 * RÉSULTAT : Le client reçoit toujours une ApiResponse bien formatée,
 * que ce soit un succès ou une erreur.
 */
@RestControllerAdvice
// @RestControllerAdvice = @ControllerAdvice + @ResponseBody
// Tous les retours de cette classe sont automatiquement
// sérialisés en JSON, comme dans un @RestController
public class GlobalExceptionHandler {

    // ================================================================
    // CAS 1 : RESSOURCE NON TROUVÉE → HTTP 404
    //
    // Déclenché quand : studentRepository.findById(id) retourne vide
    // et qu'on appelle .orElseThrow(() -> new ResourceNotFoundException())
    //
    // @ExceptionHandler(ResourceNotFoundException.class) :
    // C'est LE LIEN entre l'exception et cette méthode.
    // Spring lit cette annotation au démarrage et enregistre :
    // "Si ResourceNotFoundException est lancée → appelle handleResourceNotFound()"
    //
    // Le paramètre "exception" reçoit automatiquement l'objet
    // exception qui a été lancé, avec son message.
    // ================================================================
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFound(
            ResourceNotFoundException exception) {

        // exception.getMessage() → récupère le message qu'on a mis
        // quand on a lancé l'exception dans le Service :
        // throw new ResourceNotFoundException("Étudiant non trouvé avec l'id : " + id)
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)                    // 404
                .body(ApiResponse.error(exception.getMessage())); // message de l'exception
    }

    // ================================================================
    // CAS 2 : RESSOURCE DUPLIQUÉE → HTTP 409 Conflict
    //
    // Déclenché quand : on essaie de créer/modifier un étudiant
    // avec un email qui appartient déjà à un autre étudiant.
    //
    // HTTP 409 Conflict = "ta requête est valide mais entre en conflit
    // avec l'état actuel de la ressource sur le serveur"
    // C'est exactement ce qui se passe avec un email dupliqué.
    // ================================================================
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Void>> handleDuplicateResource(
            DuplicateResourceException exception) {

        return ResponseEntity
                .status(HttpStatus.CONFLICT)                     // 409
                .body(ApiResponse.error(exception.getMessage()));
    }

    // ================================================================
    // CAS 3 : ERREURS DE VALIDATION → HTTP 400 Bad Request
    //
    // COMMENT ÇA FONCTIONNE ?
    // ───────────────────────
    // Quand @Valid détecte que le DTO est invalide (ex: email manquant,
    // prénom trop court), Spring NE RENTRE PAS dans le Controller.
    // Spring lance automatiquement MethodArgumentNotValidException
    // AVANT même d'appeler la méthode du Controller.
    //
    // GESTION DES ERREURS MULTIPLES :
    // ────────────────────────────────
    // Si un étudiant envoie un body avec :
    //   - firstName vide
    //   - email invalide
    //   - dateOfBirth manquante
    //
    // Spring lance UNE SEULE MethodArgumentNotValidException
    // mais elle contient une LISTE de toutes les erreurs.
    // On les extrait avec getBindingResult().getFieldErrors()
    //
    // Résultat retourné :
    // {
    //   "success": false,
    //   "message": "Erreur de validation des données",
    //   "errors": [
    //     "firstName : Le prénom est obligatoire",
    //     "email : Le format de l'email est invalide",
    //     "dateOfBirth : La date de naissance est obligatoire"
    //   ]
    // }
    // ================================================================
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Void>> handleValidationErrors(
            MethodArgumentNotValidException exception) {

        // ÉTAPE 1 : Extraire la liste de toutes les erreurs de validation
        // getBindingResult() → contient le résultat complet de la validation
        // getFieldErrors()   → retourne List<FieldError>, une erreur par champ invalide
        List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();

        // ÉTAPE 2 : Transformer chaque FieldError en message lisible
        // FieldError contient :
        //   - getField()          → le nom du champ ("firstName", "email", etc.)
        //   - getDefaultMessage() → le message de l'annotation (@NotBlank, @Email, etc.)
        //
        // On formate : "firstName : Le prénom est obligatoire"
        List<String> errorMessages = fieldErrors
                .stream()
                .map(error -> error.getField() + " : " + error.getDefaultMessage())
                .collect(Collectors.toList());

        // ÉTAPE 3 : Retourner 400 avec la liste complète des erreurs
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)                           // 400
                .body(ApiResponse.error(
                    "Erreur de validation des données",
                    errorMessages  // liste de toutes les erreurs
                ));
    }

    // ================================================================
    // CAS 4 : ERREUR GÉNÉRIQUE → HTTP 500 Internal Server Error
    //
    // C'est le filet de sécurité : attrape TOUTE exception
    // qui n'a pas été gérée par les handlers précédents.
    //
    // Exemple : NullPointerException inattendue, erreur de base
    // de données, etc.
    //
    // IMPORTANT : On ne retourne PAS les détails techniques au client
    // pour des raisons de sécurité (pas de stack trace, pas de message
    // interne). On log l'erreur côté serveur et on retourne un
    // message générique.
    //
    // Exception.class = parent de toutes les exceptions Java
    // → attrape tout ce qui n'a pas été intercepté avant
    // ================================================================

    // ================================================================
    // CAS 5 : RUNTIME (identifiants invalides au login) → HTTP 401
    // ================================================================
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<Void>> handleRuntimeException(
            RuntimeException exception) {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(ApiResponse.error(exception.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleGenericException(
            Exception exception) {

        // En production, on logguerait ici avec un logger :
        // log.error("Erreur inattendue : ", exception);

        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)  // 500
                .body(ApiResponse.error(
                    "Une erreur interne est survenue. Veuillez réessayer plus tard. Error : " + exception.getMessage()
                    // On ne retourne PAS exception.getMessage() ici
                    // car cela pourrait exposer des infos sensibles au client
                ));
    }
}