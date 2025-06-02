package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.CalendrierDTO;
import com.example.javaprojet.services.CalendrierService;
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
@RequestMapping("/api/calendriers")
@RequiredArgsConstructor
public class CalendrierController {

    private final CalendrierService calendrierService;

    /**
     * Créer un calendrier
     */
    @PostMapping
    @Secured("ROLE_USER")
    public ResponseEntity<CalendrierDTO> createCalendrier(@RequestBody @Valid CalendrierDTO calendrierDTO) {
        if (calendrierDTO.getId() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        CalendrierDTO created = calendrierService.createCalendrier(calendrierDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    /**
     * Obtenir un calendrier par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CalendrierDTO> getCalendrierById(@PathVariable Long id) {
        Optional<CalendrierDTO> calendrier = calendrierService.findCalendrierById(id);
        return calendrier.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Obtenir la liste de tous les calendriers
     */
    @GetMapping
    public ResponseEntity<List<CalendrierDTO>> getAllCalendriers() {
        List<CalendrierDTO> calendriers = calendrierService.findAllCalendrier();
        return ResponseEntity.ok(calendriers);
    }

    /**
     * Mettre à jour un calendrier
     */
    @PutMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<CalendrierDTO> updateCalendrier(@PathVariable Long id,
                                                          @RequestBody @Valid CalendrierDTO calendrierDTO) {
        try {
            CalendrierDTO updated = calendrierService.updateCalendrier(id, calendrierDTO);
            return ResponseEntity.ok(updated);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un calendrier
     */
    @DeleteMapping("/{id}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> deleteCalendrier(@PathVariable Long id) {
        try {
            calendrierService.deleteCalendrier(id);
            return ResponseEntity.noContent().build();
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
