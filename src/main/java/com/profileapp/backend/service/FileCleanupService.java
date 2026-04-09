package com.profileapp.backend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * FileCleanupService — supprime les fichiers physiques du disque.
 * Utilisé lors de la suppression de services, sous-services et commandes.
 *
 * L'URL complète (http://host/uploads/photos/services/uuid.jpg)
 * est convertie en chemin relatif (uploads/photos/services/uuid.jpg)
 * puis supprimée du disque.
 */
@Slf4j
@Service
public class FileCleanupService {

    /**
     * Supprime un fichier physique à partir de son URL complète ou relative.
     * Ne lève jamais d'exception — la suppression fichier est best-effort.
     */
    public void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isBlank()) return;

        try {
            String relativePath = extractRelativePath(fileUrl);
            if (relativePath == null) return;

            Path path = Paths.get(relativePath).normalize();
            /* Sécurité : vérifier que le chemin reste dans uploads/ */
            if (!path.startsWith("uploads")) {
                log.warn("Tentative de suppression hors du dossier uploads : {}", path);
                return;
            }
            if (Files.deleteIfExists(path)) {
                log.info("Fichier supprimé : {}", path);
            }
        } catch (Exception e) {
            log.warn("Impossible de supprimer le fichier {} : {}", fileUrl, e.getMessage());
        }
    }

    /**
     * Extrait le chemin relatif (uploads/...) depuis une URL complète ou relative.
     */
    private String extractRelativePath(String url) {
        /* Si c'est déjà un chemin relatif */
        if (url.startsWith("uploads/")) return url;

        /* Si c'est une URL complète : http://host:port/uploads/photos/... */
        int idx = url.indexOf("/uploads/");
        if (idx >= 0) {
            return url.substring(idx + 1); /* retire le / initial */
        }

        return null;
    }
}
