package com.example.javaprojet.Controller;

import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.services.GroupeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controller pour gérer les groupes
 */
@RestController
@RequestMapping("/groupes")
public class GroupeController {

    @Autowired
    private GroupeService groupeService;

    @PostMapping("/{idGroupe}/rejoindre")
    public ResponseEntity<String> rejoindreGroupe(
            @PathVariable Long idGroupe,
            @RequestParam Long idUserConnecter) {
        try {
            groupeService.rejoindreGroupe(idGroupe, idUserConnecter);
            return ResponseEntity.ok("Utilisateur ajouté au groupe avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @PostMapping("/creation")
    public ResponseEntity<String> creationGroupe(
            @RequestBody Groupe groupe,
            @RequestParam Long idUserConnecter) {
        try {
            groupeService.creationGroupe(groupe, idUserConnecter);
            return ResponseEntity.ok("Groupe créé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }


    @DeleteMapping("/{idGroupe}")
    public ResponseEntity<String> supprimerGroupe(
            @PathVariable Long idGroupe,
            @RequestParam Long idUserConnecter) {
        try {
            groupeService.supprimerGroupe(idGroupe, idUserConnecter);
            return ResponseEntity.ok("Groupe supprimé avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
