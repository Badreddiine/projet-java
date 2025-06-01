package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/guest")
@PreAuthorize("hasRole('GUEST')") // Applique la vérification du rôle à tout le contrôleur
public class GuestController {

    @Autowired
    private UtilisateurService utilisateurService;



    @GetMapping("/utilisateurs/{id}/projets")
    public ResponseEntity<List<Projet>> getProjetsParUtilisateur(@PathVariable Long id) {
        return ResponseEntity.ok(utilisateurService.getProjetsByUtilisateur(id));
    }

    @GetMapping("/chercher")
    public ResponseEntity<List<UtilisateurDTO>> chercherParNom(@RequestParam String nom) {
        return ResponseEntity.ok(utilisateurService.findByNom(nom));
    }
}