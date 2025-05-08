package com.example.javaprojet.services;
import com.example.javaprojet.entity.Admin;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.RoleType;
import com.example.javaprojet.repo.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AdminService {
    @Autowired
    AdminRepesitory adminRepesitory;
    @Autowired
    ProjetRepesitory projetRepesitory;
    @Autowired
    GroupeRepesitory groupeRepesitory;
    @Autowired
    ProjetRoleRepository projetRoleRepository;
    @Autowired
    UtilisateurRepesitory utilisateurRepesitory;
    @Autowired
    ProjetRoleRepository pjRoleRepository;
    @Autowired
    private RoleService roleService;
    @Transactional
    public void accepterProjet(Long idProjet, Long adminId) {
        Projet projet = projetRepesitory.findById(idProjet)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));
        Admin admin = adminRepesitory.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin non trouvé"));

        roleService.verifyRole(admin, null, RoleType.ADMIN_GLOBAL);

        if ("REJETE".equals(projet.getEtat())) {
            throw new IllegalStateException("Le projet a déjà été rejeté");
        }
        if ("ACCEPTE".equals(projet.getEtat())) {
            throw new IllegalStateException("Projet déjà accepté");
        }

        projet.setEtat("ACCEPTE");

        Groupe groupe = projet.getGroupe();
        if (groupe == null) {
            throw new IllegalStateException("Aucun groupe associé");
        }

        if (!groupe.getProjets().contains(projet)) {
            groupe.getProjets().add(projet);
            groupeRepesitory.save(groupe);
        }

        projetRepesitory.save(projet);
    }

    @Transactional
    public void rejeterProjet(Long projetId,Long adminId) {
        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));
        Utilisateur admin = adminRepesitory.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException("Admin non trouvé"));
        roleService.verifyRole(admin, null, RoleType.ADMIN_GLOBAL);

        if ("REJETE".equals(projet.getEtat())) {
            throw new RuntimeException("Le projet a déjà été rejeté.");
        }
        if ("ACCEPTE".equals(projet.getEtat())) {
            throw new RuntimeException("Impossible de rejeter un projet déjà accepté.");
        }

        projet.setEtat("REJETE");

        Groupe groupe = projet.getGroupe();
        if (groupe != null) {
            groupe.getProjets().remove(projet);
            groupeRepesitory.save(groupe);
        }
        projetRepesitory.save(projet);
    }

    @Transactional
    public void supprimerProjet(Long projetId) {
        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé avec l'ID: " + projetId));

        projetRoleRepository.deleteByProjetId(projetId);
        projetRepesitory.delete(projet);
    }


     public boolean supprimerMembre(Long idUtilisateur) {
        Optional<Utilisateur> utilisateur =utilisateurRepesitory.findById(idUtilisateur);
        if (utilisateur.isPresent()) {
            utilisateurRepesitory.delete(utilisateur.get());
            return true;
        }
        return false;
     }

    @Transactional
    public void supprimerUtilisateurDuProjet(Long projetId, Long utilisateurId) {
        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

        Utilisateur utilisateur = utilisateurRepesitory.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        roleService.verifyRole(utilisateur, projet, RoleType.ADMIN_PROJET);

        if (!projet.getMembres().contains(utilisateur)) {
            throw new IllegalStateException("L'utilisateur ne fait pas partie de ce projet");
        }

        projetRoleRepository.deleteByProjetAndUtilisateur(projet, utilisateur);

        projet.getMembres().remove(utilisateur);
        projetRepesitory.save(projet);

        utilisateur.getProjets().remove(projet);
        utilisateurRepesitory.save(utilisateur);
    }

    @Transactional
    public void accepterDemande(Long projetId, Long utilisateurId) {
        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

        Utilisateur utilisateur = utilisateurRepesitory.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (!projet.getDemandeursEnAttente().remove(utilisateur)) {
            throw new IllegalStateException("Aucune demande trouvée");
        }

        projet.getMembres().add(utilisateur);
    }

    @Transactional
    public void refuserDemande(Long projetId, Long utilisateurId) {
        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

        Utilisateur utilisateur = utilisateurRepesitory.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        roleService.verifyRole(utilisateur, projet, RoleType.ADMIN_PROJET);

        if (!projet.getDemandeursEnAttente().remove(utilisateur)) {
            throw new IllegalStateException("Aucune demande trouvée");
        }
    }

    @Transactional
    public void bannerUtilisateur(Long utilisateurId) {
        // 1. Validation de l'ID
        if (utilisateurId == null) {
            throw new IllegalArgumentException("L'ID utilisateur ne peut pas être null");
        }

        Utilisateur utilisateur = utilisateurRepesitory.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        roleService.verifyRole(utilisateur, null, RoleType.ADMIN_GLOBAL);

        projetRoleRepository.deleteByUtilisateurId(utilisateurId);
        groupeRepesitory.retirerUtilisateurDeTousLesGroupes(utilisateurId);
        utilisateurRepesitory.delete(utilisateur);
    }

}