package com.example.javaprojet.Controller;
import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.entity.Ressource;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.ListeDiffusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/listes-diffusion")
public class ListeDiffusionController {

    private final ListeDiffusionService listeDiffusionService;

    @Autowired
    public ListeDiffusionController(ListeDiffusionService listeDiffusionService) {
        this.listeDiffusionService = listeDiffusionService;
    }

    @PostMapping
    public ResponseEntity<ListeDiffusion> createListeDiffusion(@RequestBody ListeDiffusion liste) {
        ListeDiffusion created = listeDiffusionService.create(liste);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListeDiffusion> getListeDiffusionById(@PathVariable Long id) {
        return listeDiffusionService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ListeDiffusion>> getAllListesDiffusion() {
        List<ListeDiffusion> listes = listeDiffusionService.findAll();
        return ResponseEntity.ok(listes);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ListeDiffusion> updateListeDiffusion(@PathVariable Long id, @RequestBody ListeDiffusion liste) {
        try {
            ListeDiffusion updated = listeDiffusionService.update(id, liste);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteListeDiffusion(@PathVariable Long id) {
        try {
            listeDiffusionService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/abonnes")
    public ResponseEntity<Set<Utilisateur>> getAbonnes(@PathVariable Long id) {
        try {
            Set<Utilisateur> abonnes = listeDiffusionService.getAbonnes(id);
            return ResponseEntity.ok(abonnes);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/ressources-publiques")
    public ResponseEntity<List<Ressource>> getRessourcesPubliques() {
        List<Ressource> ressources = listeDiffusionService.getRessourcesPubliques();
        return ResponseEntity.ok(ressources);
    }
}
