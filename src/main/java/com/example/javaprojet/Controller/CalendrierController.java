package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.CalendrierDTO;
import com.example.javaprojet.entity.UserPrincipal;
import com.example.javaprojet.services.CalendrierService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/calendriers")
@RequiredArgsConstructor
@Slf4j
public class CalendrierController {

    private final CalendrierService calendrierService;

    /**
     * Créer un calendrier associé à l'utilisateur connecté
     */
    @PostMapping
    public ResponseEntity<CalendrierDTO> createCalendrier(
            @RequestBody @Valid CalendrierDTO calendrierDTO,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {

        log.info("=== CREATE CALENDRIER START ===");

        try {
            // Check authentication
            if (userPrincipal == null) {
                log.error("UserPrincipal is null - user not authenticated");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }

            log.info("UserPrincipal: {}", userPrincipal);
            log.info("UserId: {}", userPrincipal.getId());
            log.info("CalendrierDTO: {}", calendrierDTO);

            // Validate userId
            Long userId = userPrincipal.getId();
            if (userId == null) {
                log.error("UserId is null");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }

            // Check if ID is already set (shouldn't be for creation)
            if (calendrierDTO.getId() != null) {
                log.warn("CalendrierDTO already has an ID: {}", calendrierDTO.getId());
                return ResponseEntity.status(HttpStatus.CONFLICT).build();
            }

            // Create calendrier
            CalendrierDTO created = calendrierService.createCalendrier(calendrierDTO, userId);
            log.info("Calendrier created successfully: {}", created);

            return ResponseEntity.status(HttpStatus.CREATED).body(created);

        } catch (EntityNotFoundException e) {
            log.error("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

        } catch (DataIntegrityViolationException e) {
            log.error("Database constraint violation: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        } catch (Exception e) {
            log.error("Unexpected error creating calendrier", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
    /**
     * Obtenir un calendrier par son ID
     * recuperer idd dans front end
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
    @PutMapping
    @Secured("ROLE_USER")
    public ResponseEntity<CalendrierDTO> updateCalendrier(@RequestBody @Valid CalendrierDTO calendrierDTO) {
        Long id = calendrierDTO.getId();
        try {
            // Set the ID from the path parameter
            calendrierDTO.setId(id);
            CalendrierDTO updated = calendrierService.updateCalendrier(calendrierDTO);
            return ResponseEntity.ok(updated);
        } catch (ChangeSetPersister.NotFoundException e) {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * Supprimer un calendrier par sont id
     * recuperrer id dans front end
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