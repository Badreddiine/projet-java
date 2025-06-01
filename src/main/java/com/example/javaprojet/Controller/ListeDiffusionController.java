package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.ListDiffusionDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Ressource;
import com.example.javaprojet.services.ListeDiffusionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/listes-diffusion")
public class ListeDiffusionController {

    private final ListeDiffusionService listeDiffusionService;

    @Autowired
    public ListeDiffusionController(ListeDiffusionService listeDiffusionService) {
        this.listeDiffusionService = listeDiffusionService;
    }

    @Secured("ROLE_ADMIN_PROJET")
    @PostMapping
    public ResponseEntity<ListDiffusionDTO> createListeDiffusion(@RequestBody ListDiffusionDTO listeDTO) {
        ListDiffusionDTO created = listeDiffusionService.create(listeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListDiffusionDTO> getListeDiffusionById(@PathVariable Long id) {
        // Create a DTO with just the ID to pass to the service
        ListDiffusionDTO searchDTO = new ListDiffusionDTO(id);
        Optional<ListDiffusionDTO> result = listeDiffusionService.findById(searchDTO);
        return result.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<ListDiffusionDTO>> getAllListesDiffusion() {
        List<ListDiffusionDTO> listes = listeDiffusionService.findAll();
        return ResponseEntity.ok(listes);
    }

    @Secured("ROLE_ADMIN_PROJET")
    @PutMapping
    public ResponseEntity<ListDiffusionDTO> updateListeDiffusion(@RequestBody ListDiffusionDTO listeDTO) {
        try {
            ListDiffusionDTO updated = listeDiffusionService.update(listeDTO);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @Secured("ROLE_ADMIN_PROJET")
    @DeleteMapping
    public ResponseEntity<Void> deleteListeDiffusion(@RequestBody ListDiffusionDTO listeDTO) {
        try {
            listeDiffusionService.delete(listeDTO);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}/abonnes")
    public ResponseEntity<Set<UtilisateurDTO>> getAbonnes(@PathVariable Long id) {
        try {
            Set<UtilisateurDTO> abonnes = listeDiffusionService.getAbonnes(new ListDiffusionDTO(id));
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