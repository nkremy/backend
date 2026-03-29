package com.profileapp.backend.repository.auth;

import com.profileapp.backend.entity.auth.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {

    // Recherche par email — utilisé au login et dans JwtFilter
    Optional<Admin> findByEmail(String email);

    // Vérification unicité email — utilisé au register
    boolean existsByEmail(String email);
}
