package com.example.javaprojet.repo;

import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.ProjetRole;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;

    public interface ProjetRoleRepository extends JpaRepository<ProjetRole, Long> {
        void deleteByProjetId(Long projetId);
        boolean deleteByProjetAndUtilisateur(Projet projet, Utilisateur utilisateur);
        boolean deleteByUtilisateurId(Long utilisateurId);
        boolean existsByUtilisateurAndProjetAndRole(Utilisateur user, Projet projet, RoleType role);
        boolean existsByUtilisateurAndRole(Utilisateur user, RoleType role);
}
