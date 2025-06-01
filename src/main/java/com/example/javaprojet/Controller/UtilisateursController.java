package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.services.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
public class UtilisateursController {

    @Autowired
    private UtilisateurService utilisateurService;

    @PostMapping("/connexion")
    public ResponseEntity<String> seConnecter(@RequestParam String email, @RequestParam String motDePasse) {
        boolean success = utilisateurService.seConnecter(email, motDePasse);
        return success
                ? ResponseEntity.ok("Connexion réussie.")
                : ResponseEntity.status(401).body("Email ou mot de passe incorrect.");
    }

    @PostMapping("/{id}/deconnexion")
    public ResponseEntity<String> seDeconnecter(@PathVariable Long id) {
        boolean success = utilisateurService.seDeconnecter(id);
        return success
                ? ResponseEntity.ok("Déconnexion réussie.")
                : ResponseEntity.status(404).body("Utilisateur non trouvé.");
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> modifierProfil(@PathVariable Long id, @RequestBody UtilisateurDTO utilisateurModifie) {
        try {
            utilisateurService.modifierProfil(id, utilisateurModifie);
            return ResponseEntity.ok("Profil mis à jour avec succès.");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> getUtilisateur(@PathVariable Long id) {
        UtilisateurDTO utilisateur = utilisateurService.getUtilisateurById(id);
        return utilisateur != null
                ? ResponseEntity.ok(utilisateur)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/projets")
    public ResponseEntity<List<Projet>> getProjets(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getProjetsByUtilisateur(id));
    }

    @PostMapping("/{id}/projets")
    public ResponseEntity<String> demanderCreationProjet(@RequestBody Projet projet) {
        try {
            utilisateurService.demanderCreationProjet(projet);
            return ResponseEntity.ok("Demande de création de projet envoyée.");
        } catch (IllegalArgumentException | EntityNotFoundException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @PostMapping("/{userId}/rejoindre-projet/{projetId}")
    public ResponseEntity<String> demanderRejoindreProjet(@PathVariable Long userId, @PathVariable Long projetId) {
        try {
            utilisateurService.demanderRejoindreProjet(projetId, userId);
            return ResponseEntity.ok("Demande envoyée pour rejoindre le projet.");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (IllegalStateException e) {
            return ResponseEntity.status(409).body(e.getMessage());
        }
    }

    @GetMapping("/chercher")
    public ResponseEntity<List<UtilisateurDTO>> chercherParNom(@RequestParam String nom) {
        List<UtilisateurDTO> utilisateurs = utilisateurService.findByNom(nom);
        return ResponseEntity.ok(utilisateurs);
    }

    @PutMapping("/{id}/changer-role")
    public ResponseEntity<String> changerRole(@PathVariable Long id, @RequestParam RoleType role) {
        boolean updated = utilisateurService.changerRole(id, role);
        return updated
                ? ResponseEntity.ok("Rôle mis à jour.")
                : ResponseEntity.status(404).body("Utilisateur non trouvé.");
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerCompte(@PathVariable Long id) {
        boolean deleted = utilisateurService.supprimerCompte(id);
        return deleted
                ? ResponseEntity.ok("Compte supprimé.")
                : ResponseEntity.status(404).body("Utilisateur non trouvé.");
    }

    @GetMapping("/{projetId}/membres")
    public ResponseEntity<List<UtilisateurDTO>> getMembresDuProjet(@PathVariable Long projetId, @RequestParam Long utilisateurId) {
        try {
            List<UtilisateurDTO> membres = utilisateurService.getMembresDuProjet(projetId, utilisateurId);
            return ResponseEntity.ok(membres);
        } catch (SecurityException e) {
            return ResponseEntity.status(403).build();
        }
    }

    @PostMapping("/creer-compte")
    public ResponseEntity<String> creerCompte(@RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            utilisateurService.creeCompte(utilisateurDTO);
            return ResponseEntity.ok("Compte créé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UtilisateurDTO> getUtilisateurByEmail(@PathVariable String email) {
        UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByEmail(email);
        return utilisateur != null
                ? ResponseEntity.ok(utilisateur)
                : ResponseEntity.notFound().build();
    }
}