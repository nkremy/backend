package com.profileapp.backend.exception;


/**
 * EXCEPTION : Ressource non trouvée → HTTP 404
 *
 * Lancée quand on cherche un étudiant par ID ou email
 * et qu'il n'existe pas en base de données.
 *
 * extends RuntimeException → pas besoin de la déclarer
 * dans la signature des méthodes avec "throws"
 */
public class ResourceNotFoundException extends RuntimeException {

    // ================================================================
    // CONSTRUCTEUR
    // On passe un message descriptif qui sera retourné au client.
    // Ex: "Étudiant non trouvé avec l'id : 5"
    // ================================================================
    public ResourceNotFoundException(String message) {
        super(message);
    }
}