package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.CalendrierDTO;
import com.example.javaprojet.dto.EvenementDTO;
import com.example.javaprojet.services.EvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/evenements")
@RequiredArgsConstructor
public class EvenementController {
    private final EvenementService evenementService;

    /**
     * Créer un événement
     * La création d'événement - seul l'Admin de projet peut créer des événements
     */
    @PostMapping
    @Secured("ROLE_ADMIN_PROJET")
    public ResponseEntity<EvenementDTO> createEvenement(@RequestBody @Valid EvenementDTO evenementDTO,
                                                        @RequestParam @Valid CalendrierDTO calendrierDTO) {

        if (evenementDTO.getId() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        EvenementDTO created = evenementService.createEvenement(evenementDTO, calendrierDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Récupérer un événement par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<EvenementDTO> getEvenement(@PathVariable Long id) {
        Optional<EvenementDTO> evenement = evenementService.findEvenementById(id);
        return evenement.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Mettre à jour un événement
     */
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN_PROJET")
    public ResponseEntity<EvenementDTO> updateEvenement(@PathVariable Long id,
                                                        @RequestBody @Valid EvenementDTO evenementDTO) {
        try {
            EvenementDTO updated = evenementService.updateEvenement(id, evenementDTO);
            return ResponseEntity.ok(updated);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un événement
     */
    @DeleteMapping("/{id}")
    @Secured("ROLE_ADMIN_PROJET")
    public ResponseEntity<Void> deleteEvenement(@PathVariable Long id) {
        try {
            evenementService.deleteEvenement(id);
            return ResponseEntity.noContent().build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
