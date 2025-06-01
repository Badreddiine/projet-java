package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.TacheDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Tache;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.TacheService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/taches")
public class TacheController {

    @Autowired
    private TacheService tacheService;

    @Secured({"ROLE_ADMIN_PROJET"})
    @PostMapping
    public ResponseEntity<Tache> creerTache(@RequestBody @Valid TacheDTO tache) {
        Tache nouvelleTache = tacheService.creerTache(tache);
        return ResponseEntity.ok(nouvelleTache);
    }
    @Secured({ "ROLE_ADMIN_PROJET"})
    @PutMapping("/{id}")
    public ResponseEntity<Tache> mettreAJourTache( @RequestBody @Valid TacheDTO tacheModifiee) {
        try {
            Tache tacheMiseAJour = tacheService.mettreAJourTache( tacheModifiee);
            return ResponseEntity.ok(tacheMiseAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }
    @Secured({"ROLE_ADMIN_PROJET"})
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> supprimerTache(@PathVariable @Valid TacheDTO tacheDTO) {
        try {
            tacheService.supprimerTache(tacheDTO);
            return ResponseEntity.noContent().build(); // Retourne 204 No Content
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<Tache> recupererTacheParId(@PathVariable @Valid TacheDTO tacheDTO) {

        try {
            Tache tache = tacheService.recupererTacheParId(tacheDTO);
            return ResponseEntity.ok(tache);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }

    @GetMapping
    public ResponseEntity<List<TacheDTO>> recupererToutesLesTaches() {
        List<TacheDTO> taches = tacheService.recupererToutesLesTaches();
        return ResponseEntity.ok(taches);
    }

    @GetMapping("/etat")
    public ResponseEntity<List<TacheDTO>> recupererTachesParEtat(@RequestParam String etat) {
        List<TacheDTO> taches = tacheService.recupererTachesParEtat(etat);
        return ResponseEntity.ok(taches);
    }



    @PutMapping("/{id}/etat")
    public ResponseEntity<Tache> changerEtat(@PathVariable TacheDTO tacheDTO, @RequestParam String nouvelEtat) {
        try {
            Tache tacheMiseAJour = tacheService.changerEtat(tacheDTO, nouvelEtat);
            return ResponseEntity.ok(tacheMiseAJour);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }

    @PutMapping("/{id}/assigner")
    public ResponseEntity<Tache> assignerTacheAUtilisateur(@PathVariable TacheDTO tacheDTO, @RequestBody UtilisateurDTO utilisateur) {
        try {
            Tache tacheAssignee = tacheService.assignerA(tacheDTO, utilisateur);
            return ResponseEntity.ok(tacheAssignee);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build(); // Si la tâche n'est pas trouvée
        }
    }

 /*   @Secured({"ROLE_ADMIN_PROJET"})
    @GetMapping
    public List<Tache> getAllTaches() {
        return tacheService.getTach();
    }*/
}
