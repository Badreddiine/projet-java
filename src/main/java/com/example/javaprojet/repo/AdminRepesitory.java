package com.example.javaprojet.repo;

import com.example.javaprojet.entity.Admin;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;
import java.util.Set;
public interface AdminRepesitory extends JpaRepository<Admin, Long> {
   // List<Admin> findByRole(RoleType role); // Utilisez l'enum comme paramètre

    // Méthode standard par ID
    Optional<Admin> findById(Long id);
}
