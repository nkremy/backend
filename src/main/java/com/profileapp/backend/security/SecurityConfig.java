package com.profileapp.backend.security;

import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * SecurityConfig — orchestre toute la chaîne de sécurité.
 *
 * Ordre final des filtres après configuration :
 * 1. SecurityContextPersistenceFilter (Spring — crée le contexte vide)
 * 2. JwtFilter ← notre filtre admin (inséré en second)
 * 3. ApiKeyFilter ← notre filtre agent (inséré en premier)
 * 4. UsernamePasswordAuthenticationFilter (Spring — désactivé en pratique)
 * 5. ExceptionTranslationFilter (Spring — convertit exceptions → 401/403)
 * 6. FilterSecurityInterceptor (Spring — vérifie les règles d'accès)
 * ↓ Controller
 *
 * ApiKeyFilter avant JwtFilter : l'agent est authentifié en premier.
 * Si les deux headers sont présents, ApiKeyFilter authentifie et JwtFilter
 * voit le contexte déjà rempli — il ne fait rien.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final ApiKeyFilter apiKeyFilter;

    // Bean PasswordEncoder — instance unique partagée dans tout le projet
    // BCryptPasswordEncoder(10) : force 10 = 2^10 = 1024 itérations
    // Suffisamment lent pour résister aux attaques brute-force
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // Désactiver CSRF — inutile avec JWT (pas de cookie, pas de session)
                .csrf(AbstractHttpConfigurer::disable)

                // cofiguration du cors
                .cors(cors -> cors
                        .configurationSource(corsConfigurationSource()))

                // Désactiver les sessions HTTP — JWT est stateless
                // Chaque requête s'auto-authentifie via son token
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Règles d'accès — évaluées dans l'ordre, première correspondance gagne
                .authorizeHttpRequests(auth -> auth
                        // Endpoints publics — login et register sans token
                        .requestMatchers("/api/v1/auth/**").permitAll()
                        // Tout le reste exige une authentification (JWT ou clé API)
                        .anyRequest().authenticated())

                // Insérer nos filtres AVANT UsernamePasswordAuthenticationFilter
                // ApiKeyFilter en premier pour authentifier les agents
                // JwtFilter en second pour authentifier les admins
                .addFilterBefore(apiKeyFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtFilter, ApiKeyFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(List.of("https://gno-crm-frontend.netlify.app"));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        config.setAllowedHeaders(List.of("Authorization", "Content-Type", "X-API-Key"));
        // config.setAllowCredentials(true); // CRUCIAL pour les cookies
        System.err.println("*******************************");
        System.err.println("cofigure le Cors");
        System.out.println(config);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
