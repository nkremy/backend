package com.profileapp.backend.controller;

import com.profileapp.backend.util.ApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * UploadController — Upload multipart + exposition des images.
 *
 * L'URL retournée est une URL COMPLÈTE construite dynamiquement :
 *   - En local  : http://localhost:8082/uploads/photos/services/uuid.jpg
 *   - En prod   : https://mondomaine.com/uploads/photos/services/uuid.jpg
 *
 * Grâce à ServletUriComponentsBuilder, le host/port/scheme sont
 * détectés automatiquement depuis la requête HTTP courante.
 * WebConfig.addResourceHandlers sert les fichiers statiques.
 */
@RestController
@RequestMapping("/api/v1/upload")
public class UploadController {

    @Value("${app.upload.services.dir:uploads/photos/services}")
    private String servicesDir;

    @Value("${app.upload.sous-services.dir:uploads/photos/sous-services}")
    private String sousServicesDir;

    @Value("${app.upload.commandes.dir:uploads/photos/commandes}")
    private String commandesDir;

    /* ── Image de couverture d'un service ───────────────────────── */
    @PostMapping("/service-image")
    public ResponseEntity<ApiResponse<String>> uploadServiceImage(
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = saveFile(file, servicesDir);
        return ResponseEntity.ok(ApiResponse.success("Image uploadée", url));
    }

    /* ── Images d'un sous-service (1 ou plusieurs) ──────────────── */
    @PostMapping("/sous-service-images")
    public ResponseEntity<ApiResponse<List<String>>> uploadSousServiceImages(
            @RequestParam("files") List<MultipartFile> files) throws IOException {
        List<String> urls = new ArrayList<>();
        for (MultipartFile file : files) {
            urls.add(saveFile(file, sousServicesDir));
        }
        return ResponseEntity.ok(ApiResponse.success("Images uploadées", urls));
    }

    /* ── Document de commande ───────────────────────────────────── */
    @PostMapping("/commande-document")
    public ResponseEntity<ApiResponse<String>> uploadCommandeDocument(
            @RequestParam("file") MultipartFile file) throws IOException {
        String url = saveFile(file, commandesDir);
        return ResponseEntity.ok(ApiResponse.success("Document uploadé", url));
    }

    /* ── Suppression d'un fichier orphelin ───────────────────────── */
    @DeleteMapping("/delete")
    public ResponseEntity<ApiResponse<Void>> deleteFile(
            @RequestParam("path") String filePath) {
        try {
            /* Extraire le chemin relatif depuis l'URL complète si nécessaire */
            String relativePath = filePath;
            if (filePath.contains("/uploads/")) {
                relativePath = filePath.substring(filePath.indexOf("/uploads/") + 1);
            }
            Path path = Paths.get(relativePath).normalize();
            if (!path.startsWith("uploads/")) {
                return ResponseEntity.badRequest()
                        .body(ApiResponse.error("Chemin non autorisé"));
            }
            Files.deleteIfExists(path);
            return ResponseEntity.ok(ApiResponse.success("Fichier supprimé"));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.success("Fichier non trouvé ou déjà supprimé"));
        }
    }

    /* ── Sauvegarde + construction URL complète ─────────────────── */
    private String saveFile(MultipartFile file, String dirPath) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Le fichier est vide");
        }
        Path dir = Paths.get(dirPath);
        Files.createDirectories(dir);

        String originalName = file.getOriginalFilename();
        String extension = (originalName != null && originalName.contains("."))
                ? originalName.substring(originalName.lastIndexOf("."))
                : "";
        String fileName = UUID.randomUUID() + extension;

        Path destination = dir.resolve(fileName);
        Files.copy(file.getInputStream(), destination, StandardCopyOption.REPLACE_EXISTING);

        /* ═══ EXPOSITION — URL complète construite dynamiquement ═══
         * ServletUriComponentsBuilder lit le host/port/scheme de la requête
         * HTTP courante et construit une URL absolue.
         *   Local : http://localhost:8082/uploads/photos/services/uuid.jpg
         *   Prod  : https://api.mondomaine.com/uploads/photos/services/uuid.jpg
         */
        String fullUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/" + dirPath + "/" + fileName)
                .toUriString();

        return fullUrl;
    }
}
