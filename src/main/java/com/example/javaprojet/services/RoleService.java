package com.example.javaprojet.services;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.repo.ProjetRoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    @Autowired
     ProjetRoleRepository projetRoleRepository;


    public void verifyRole(Utilisateur user, Projet projet, RoleType requiredRole) {
        boolean hasAccess;
        if (projet != null) {
            hasAccess = projetRoleRepository.existsByUtilisateurAndProjetAndRole(user, projet, requiredRole) ||
                    projetRoleRepository.existsByUtilisateurAndRole(user, RoleType.ADMIN);
        } else {
            hasAccess = projetRoleRepository.existsByUtilisateurAndRole(user, requiredRole);
        }

        if (!hasAccess) {
            throw new SecurityException("Accès refusé. Rôle requis: " + requiredRole);
        }
    }
    public boolean hasRole(Utilisateur user, Projet projet, RoleType requiredRole) {
        // ADMIN_GLOBAL bypass toutes les vérifications
        if (projetRoleRepository.existsByUtilisateurAndRole(user, RoleType.ADMIN)) {
            return true;
        }
        return projet != null
                ? projetRoleRepository.existsByUtilisateurAndProjetAndRole(user, projet, requiredRole)
                : projetRoleRepository.existsByUtilisateurAndRole(user, requiredRole);
    }
}