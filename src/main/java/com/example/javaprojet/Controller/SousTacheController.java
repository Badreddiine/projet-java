package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.SousTacheDTO;
import com.example.javaprojet.entity.SousTache;
import com.example.javaprojet.services.SousTacheService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/sous-taches")
@RequiredArgsConstructor
public class SousTacheController {

    private final SousTacheService sousTacheService;

    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN_PROJET')")
    public ResponseEntity<SousTache> creerSousTache(@RequestBody SousTacheDTO sousTacheDTO) {
        SousTache sousTache = new SousTache(sousTacheDTO);
        SousTache nouvelleSousTache = sousTacheService.creerSousTache(sousTache);
        return ResponseEntity.status(HttpStatus.CREATED).body(nouvelleSousTache);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN_PROJET')")
    public ResponseEntity<SousTache> mettreAJourSousTache(
            @PathVariable Long id,
            @RequestBody SousTacheDTO sousTacheDTO) {
        SousTache sousTacheModifiee = sousTacheService.mettreAJourSousTache(id, sousTacheDTO);
        return ResponseEntity.ok(sousTacheModifiee);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN_PROJET')")
    public ResponseEntity<Void> supprimerSousTache(@PathVariable Long id) {
        sousTacheService.supprimerSousTache(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN_PROJET')")
    public ResponseEntity<SousTache> getSousTacheById(@PathVariable Long id) {
        SousTache sousTache = sousTacheService.recupererSousTacheParId(id);
        return ResponseEntity.ok(sousTache);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN_PROJET', 'ADMIN')")
    public ResponseEntity<List<SousTache>> getAllSousTaches() {
        List<SousTache> sousTaches = sousTacheService.recupererToutesLesSousTaches();
        return ResponseEntity.ok(sousTaches);
    }

    @GetMapping("/etat/{etat}")
    @PreAuthorize("hasAnyRole('ADMIN_PROJET', 'ADMIN')")
    public ResponseEntity<List<SousTache>> getSousTachesByEtat(@PathVariable String etat) {
        List<SousTache> sousTaches = sousTacheService.recupererSousTachesParEtat(etat);
        return ResponseEntity.ok(sousTaches);
    }

    @PatchMapping("/{id}/etat")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN_PROJET')")
    public ResponseEntity<SousTache> changerEtatSousTache(
            @PathVariable Long id,
            @RequestParam String nouvelEtat) {
        SousTache sousTache = sousTacheService.changerEtatSousTache(id, nouvelEtat);
        return ResponseEntity.ok(sousTache);
    }
}

