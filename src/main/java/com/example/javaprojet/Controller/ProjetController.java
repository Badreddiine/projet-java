package com.example.javaprojet.Controller;

import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.ProjetService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projets")
public class ProjetController {

    @Autowired
    private ProjetService projetService;
    @Secured("ROLE_ADMIN")
    @PostMapping("/{projetId}/accepter")
    public ResponseEntity<String> accepterProjet(
            @PathVariable Long projetId,
            @RequestParam Long idUserConnecter) {
        try {
            projetService.accepterProjet(projetId, idUserConnecter);
            return ResponseEntity.ok("Projet accepté avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Secured("ROLE_ADMIN")
    @PostMapping("/{projetId}/rejeter")
    public ResponseEntity<String> rejeterProjet(
            @PathVariable Long projetId,
            @RequestParam Long idUserConnecter) {
        try {
            projetService.rejeterProjet(projetId, idUserConnecter);
            return ResponseEntity.ok("Projet rejeté avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @Secured({"ROLE_ADMIN","ROLE_ADMIN_PROJET"})

    @DeleteMapping("/{projetId}")
    public ResponseEntity<String> supprimerProjet(
            @PathVariable Long projetId,
            @RequestParam Long adminId) {
        try {
            projetService.supprimerProjet(projetId, adminId);
            return ResponseEntity.ok("Projet supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @Secured({"ROLE_ADMIN", "ROLE_ADMIN_PROJET"})
    @DeleteMapping("/{projetId}/utilisateurs/{utilisateurId}")
    public ResponseEntity<String> supprimerUtilisateurDuProjet(
            @PathVariable Long projetId,
            @PathVariable Long utilisateurId,
            @RequestParam Long idUserConnecter) {
        try {
            projetService.supprimerUtilisateurDuProjet(projetId, utilisateurId, idUserConnecter);
            return ResponseEntity.ok("Utilisateur supprimé du projet");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Secured({"ROLE_ADMIN_PROJET"})
    @PostMapping("/{projetId}/demandes/{demandeurId}/accepter")
    public ResponseEntity<String> accepterDemande(
            @PathVariable Long projetId,
            @PathVariable Long demandeurId,
            @RequestParam Long idUserConnecter) {
        try {
            projetService.accepterDemande(projetId, demandeurId, idUserConnecter);
            return ResponseEntity.ok("Demande acceptée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Secured({"ROLE_ADMIN_PROJET"})
    @PostMapping("/{projetId}/demandes/{demandeurId}/refuser")
    public ResponseEntity<String> refuserDemande(
            @PathVariable Long projetId,
            @PathVariable Long demandeurId,
            @RequestParam Long idUserConnecter) {
        try {
            projetService.refuserDemande(projetId, demandeurId, idUserConnecter);
            return ResponseEntity.ok("Demande refusée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
    @Secured({"ROLE_ADMIN_PROJET"})
    @GetMapping("/{projetId}/demandes")
    public ResponseEntity<List<Utilisateur>> afficherListDemandeRejoindreProjet(
            @PathVariable Long projetId,
            @RequestParam Long demandeurId,
            @RequestParam Long idUserConnecter) {
        try {
            List<Utilisateur> demandes = projetService.afficherListDemandeRejoindreProjet(projetId, demandeurId, idUserConnecter);
            return ResponseEntity.ok(demandes);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @GetMapping
    public List<Projet> getAllProjets() {
        return projetService.getProjet();
    }
}
