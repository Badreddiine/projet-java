package com.example.javaprojet.Controller;

import com.example.javaprojet.entity.Tache;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.TacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taches")
public class TacheController {

    @Autowired
    private TacheService tacheService;

    @PostMapping
    public ResponseEntity<Tache> creerTache(@RequestBody Tache tache) {
        Tache nouvelleTache = tacheService.creerTache(tache);
        return ResponseEntity.ok(nouvelleTache);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Tache> mettreAJourTache(@PathVariable Long id, @RequestBody Tache tacheModifiee) {
        try {
            Tache tacheMiseAJour = tacheService.mettreAJourTache(id, tacheModifiee);
            return ResponseEntity.ok(tacheMiseAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerTache(@PathVariable Long id) {
        try {
            tacheService.supprimerTache(id);
            return ResponseEntity.noContent().build(); // Retourne 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tache> recupererTacheParId(@PathVariable Long id) {
        try {
            Tache tache = tacheService.recupererTacheParId(id);
            return ResponseEntity.ok(tache);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }

    @GetMapping
    public ResponseEntity<List<Tache>> recupererToutesLesTaches() {
        List<Tache> taches = tacheService.recupererToutesLesTaches();
        return ResponseEntity.ok(taches);
    }

    @GetMapping("/etat")
    public ResponseEntity<List<Tache>> recupererTachesParEtat(@RequestParam String etat) {
        List<Tache> taches = tacheService.recupererTachesParEtat(etat);
        return ResponseEntity.ok(taches);
    }

    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Tache>> recupererTachesParUtilisateur(@PathVariable Long utilisateurId) {
        List<Tache> taches = tacheService.recupererTachesParUtilisateur(utilisateurId);
        return ResponseEntity.ok(taches);
    }

    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<Tache>> recupererTachesParProjet(@PathVariable Long projetId) {
        List<Tache> taches = tacheService.recupererTachesParProjet(projetId);
        return ResponseEntity.ok(taches);
    }

    @PutMapping("/{id}/etat")
    public ResponseEntity<Tache> changerEtat(@PathVariable Long id, @RequestParam String nouvelEtat) {
        try {
            Tache tacheMiseAJour = tacheService.changerEtat(id, nouvelEtat);
            return ResponseEntity.ok(tacheMiseAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }

    @PutMapping("/{id}/assigner")
    public ResponseEntity<Tache> assignerTacheAUtilisateur(@PathVariable Long id, @RequestBody Utilisateur utilisateur) {
        try {
            Tache tacheAssignee = tacheService.assignerA(id, utilisateur);
            return ResponseEntity.ok(tacheAssignee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }
}
