package com.example.javaprojet.Controller;

import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.RoleType;
import com.example.javaprojet.services.UtilisateurService;
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
    public ResponseEntity<String> modifierProfil(@PathVariable Long id, @RequestBody Utilisateur utilisateurModifie) {
        utilisateurService.modifierProfil(id, utilisateurModifie);
        return ResponseEntity.ok("Profil mis à jour avec succès.");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> getUtilisateur(@PathVariable Long id) {
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(id);
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
        utilisateurService.demanderCreationProjet(projet);
        return ResponseEntity.ok("Demande de création de projet envoyée.");
    }

    @PostMapping("/{userId}/rejoindre-projet/{projetId}")
    public ResponseEntity<String> demanderRejoindreProjet(@PathVariable Long userId, @PathVariable Long projetId) {
        utilisateurService.demanderRejoindreProjet(projetId, userId);
        return ResponseEntity.ok("Demande envoyée pour rejoindre le projet.");
    }

    @GetMapping("/chercher")
    public ResponseEntity<List<Utilisateur>> chercherParNom(@RequestParam String nom) {
        return ResponseEntity.ok(utilisateurService.findByNom(nom));
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
}
