package com.profileapp.backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/**
 * JwtUtil — responsable de la génération et de la validation des tokens JWT.
 *
 * Deux responsabilités uniquement :
 *   1. generateToken(email) → fabrique un JWT signé
 *   2. validateToken(token) → vérifie signature + expiration
 *   3. extractEmail(token)  → lit le subject du payload
 *   4. getExpirationFromToken(token) → lit la date d'expiration (pour le logout)
 */
@Component
public class JwtUtil {

    @Value("${app.jwt.secret}")
    private String secret;

    @Value("${app.jwt.expiration}")
    private long expiration;

    // Construit la SecretKey à partir de la String lue dans application.properties
    // Keys.hmacShaKeyFor exige au minimum 32 octets (256 bits pour HS256)
    // Lance une exception au démarrage si la clé est trop courte — sécurité garantie
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // ── Génération ─────────────────────────────────────────────────────
    // Pattern Builder JJWT : chaque méthode configure une partie du token
    // et retourne le même JwtBuilder. .compact() déclenche la construction réelle.
    public String generateToken(String email) {
        Date maintenant = new Date();
        Date dateExpiration = new Date(maintenant.getTime() + expiration);

        return Jwts.builder()
                .subject(email)           // "sub" : identifiant de l'admin dans le payload
                .issuedAt(maintenant)     // "iat" : date de création
                .expiration(dateExpiration) // "exp" : date d'expiration — vérifié automatiquement au parsing
                .signWith(getSigningKey()) // calcule la signature HMAC-SHA256
                .compact();               // encode en Base64 et concatène header.payload.signature
    }

    // ── Extraction de l'email ───────────────────────────────────────────
    // Retourne le "sub" du payload — l'email mis lors de la génération
    public String extractEmail(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey()) // clé pour vérifier la signature
                .build()
                .parseSignedClaims(token)   // vérifie signature + expiration, retourne Jws<Claims>
                .getPayload()               // l'objet Claims = le payload décodé
                .getSubject();              // lit "sub"
    }

    // ── Validation complète ─────────────────────────────────────────────
    // Retourne true si le token est valide (signature OK + non expiré)
    // Retourne false pour tout autre cas (expiré, falsifié, malformé)
    // N'expose pas d'exception — simplifie le code du filtre
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    // ── Date d'expiration ───────────────────────────────────────────────
    // Utilisé lors du logout pour remplir expiresAt dans TokenBlacklist
    // Permet de savoir jusqu'à quand garder le token blacklisté
    public LocalDateTime getExpirationFromToken(String token) {
        Date expDate = Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration();

        // Conversion Date (java.util ancien) → LocalDateTime (java.time moderne)
        return expDate.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
    }
}
