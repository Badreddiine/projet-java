package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.EvenementDTO;
import com.example.javaprojet.services.EvenementService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
    public ResponseEntity<EvenementDTO> createEvenement(@RequestBody @Valid EvenementDTO evenementDTO) {
        try {
            System.out.println("Received EvenementDTO: " + evenementDTO);

            if (evenementDTO.getId() != null) {
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Vérifier que calendrierId est fourni
            if (evenementDTO.getCalendrierId() == null) {
                return ResponseEntity.badRequest().build();
            }

            EvenementDTO created = evenementService.createEvenement(evenementDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(created);
        } catch (Exception e) {
            System.err.println("Erreur lors de la création de l'événement: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
     * Récupérer tous les événements
     */
    @GetMapping
    public ResponseEntity<List<EvenementDTO>> getAllEvenements() {
        return ResponseEntity.ok(evenementService.findAllEvenements());
    }

    /**
     * Mettre à jour un événement
     */
    @PutMapping("/{id}")
    @Secured("ROLE_ADMIN_PROJET")
    public ResponseEntity<EvenementDTO> updateEvenement(
            @PathVariable Long id,
            @RequestBody @Valid EvenementDTO evenementDTO) {

        // S'assurer que l'ID dans l'URL correspond à celui du DTO
        evenementDTO.setId(id);

        try {
            EvenementDTO updated = evenementService.updateEvenement(evenementDTO);
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