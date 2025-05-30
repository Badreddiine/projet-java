package com.example.javaprojet.Controller;

import com.example.javaprojet.entity.SousTache;
import com.example.javaprojet.services.SousTacheService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/sous-taches")
public class SousTacheController {

    @Autowired
    private SousTacheService sousTacheService;


    @PostMapping
    public ResponseEntity<SousTache> creerSousTache(@RequestBody SousTache sousTache) {
        try {
            SousTache nouvelleSousTache = sousTacheService.creerSousTache(sousTache);
            return ResponseEntity.ok(nouvelleSousTache);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<SousTache> mettreAJourSousTache(
            @PathVariable Long id,
            @RequestBody SousTache sousTacheModifiee) {
        try {
            SousTache sousTache = sousTacheService.mettreAJourSousTache(id, sousTacheModifiee);
            return ResponseEntity.ok(sousTache);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerSousTache(@PathVariable Long id) {
        try {
            sousTacheService.supprimerSousTache(id);
            return ResponseEntity.ok("Sous-tâche supprimée avec succès");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Erreur lors de la suppression");
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<SousTache> recupererSousTacheParId(@PathVariable Long id) {
        try {
            SousTache sousTache = sousTacheService.recupererSousTacheParId(id);
            return ResponseEntity.ok(sousTache);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @GetMapping
    public ResponseEntity<List<SousTache>> recupererToutesLesSousTaches() {
        List<SousTache> sousTaches = sousTacheService.recupererToutesLesSousTaches();
        return ResponseEntity.ok(sousTaches);
    }


    @GetMapping("/par-tache/{tacheId}")
    public ResponseEntity<List<SousTache>> recupererSousTachesParTache(@PathVariable Long tacheId) {
        List<SousTache> sousTaches = sousTacheService.recupererSousTachesParTache(tacheId);
        return ResponseEntity.ok(sousTaches);
    }

    @GetMapping("/par-etat")
    public ResponseEntity<List<SousTache>> recupererSousTachesParEtat(@RequestParam String etat) {
        List<SousTache> sousTaches = sousTacheService.recupererSousTachesParEtat(etat);
        return ResponseEntity.ok(sousTaches);
    }


    @PutMapping("/{id}/changer-etat")
    public ResponseEntity<SousTache> changerEtatSousTache(
            @PathVariable Long id,
            @RequestParam String nouvelEtat) {
        try {
            SousTache sousTache = sousTacheService.changerEtatSousTache(id, nouvelEtat);
            return ResponseEntity.ok(sousTache);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    @PutMapping("/{id}/terminer")
    public ResponseEntity<SousTache> marquerCommeTerminee(
            @PathVariable Long id,
            @RequestParam boolean estTerminee) {
        try {
            SousTache sousTache = sousTacheService.marquerCommeTerminee(id, estTerminee);
            return ResponseEntity.ok(sousTache);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
/*
    @GetMapping
    public List<SousTache> getAllSousTaches() {
        return sousTacheService.getSousTaches();
    }*/
}
