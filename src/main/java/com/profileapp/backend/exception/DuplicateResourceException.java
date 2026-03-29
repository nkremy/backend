package com.profileapp.backend.exception;


/**
 * EXCEPTION : Ressource dupliquée → HTTP 409 Conflict
 *
 * Lancée quand on essaie de créer ou modifier un étudiant
 * avec un email qui appartient déjà à un autre étudiant.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}