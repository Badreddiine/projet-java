package com.example.javaprojet.Controller;


import com.example.javaprojet.dto.ReunionDTO;
import com.example.javaprojet.entity.Reunion;
import com.example.javaprojet.entity.UserPrincipal;
import com.example.javaprojet.services.ReunionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/reunions")
@CrossOrigin(origins = "*")
public class ReunionController {

    @Autowired
    private ReunionService reunionService;

    /**
     * Créer une nouvelle réunion avec un projet
     * @param reunionDTO les données de la réunion à créer
     * @return la réunion créée
     */
    @PostMapping
    public ResponseEntity<Reunion> creerReunion(@RequestBody ReunionDTO reunionDTO) {
        try {
            // Convertir DTO en entité
            Reunion reunion = new Reunion(reunionDTO);
            Reunion reunionCreee = reunionService.createWithProjet(reunion, reunionDTO.getProjet().getId());
            return new ResponseEntity<>(reunionCreee, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mettre à jour une réunion existante
     * @param id l'ID de la réunion à modifier
     * @param reunionDTO les nouvelles données de la réunion
     * @return la réunion modifiée
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reunion> mettreAJourReunion(@PathVariable Long id, @RequestBody ReunionDTO reunionDTO) {
        try {
            reunionDTO.setId(id); // S'assurer que l'ID correspond
            Reunion reunionModifiee = reunionService.update(reunionDTO);
            return new ResponseEntity<>(reunionModifiee, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprimer une réunion
     * @param id l'ID de la réunion à supprimer
     * @return statut de la suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerReunion(@PathVariable Long id) {
        try {
            reunionService.delete(id);
            return new ResponseEntity<>("Réunion supprimée avec succès", HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la suppression", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer une réunion par son ID
     * @param id l'ID de la réunion
     * @return la réunion trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reunion> recupererReunionParId(@PathVariable Long id) {
        try {
            Optional<Reunion> reunion = reunionService.findById(id);
            if (reunion.isPresent()) {
                return new ResponseEntity<>(reunion.get(), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer toutes les réunions
     * @return liste de toutes les réunions
     */
    @GetMapping
    public ResponseEntity<List<Reunion>> recupererToutesLesReunions() {
        try {
            List<Reunion> reunions = reunionService.findAll();
            return new ResponseEntity<>(reunions, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Ajouter un participant à une réunion
     * @param reunionId l'ID de la réunion
     * @param utilisateurId l'ID de l'utilisateur à ajouter
     * @return la réunion avec le nouveau participant
     */
    @PostMapping("/{reunionId}/participants/{utilisateurId}")
    public ResponseEntity<Reunion> ajouterParticipant(@PathVariable Long reunionId, @PathVariable Long utilisateurId) {
        try {
            Reunion reunion = reunionService.ajouterParticipant(reunionId, utilisateurId);
            return new ResponseEntity<>(reunion, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Ajouter un participant via un objet JSON
     * @param reunionId l'ID de la réunion
     * @return la réunion avec le nouveau participant
     */
    @PostMapping("/{reunionId}/participants")
    public ResponseEntity<Reunion> ParticiperAmeet(@PathVariable Long reunionId,@AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Reunion reunion = reunionService.ajouterParticipant(reunionId, userPrincipal.getId());
            return new ResponseEntity<>(reunion, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Créer une réunion avec un projet spécifique (endpoint alternatif)
     * @param projetId l'ID du projet
     * @param reunionDTO les données de la réunion
     * @return la réunion créée
     */
    @PostMapping("/projet/{projetId}")
    public ResponseEntity<Reunion> creerReunionPourProjet(@PathVariable Long projetId, @RequestBody ReunionDTO reunionDTO) {
        try {
            Reunion reunion =new Reunion(reunionDTO);
            Reunion reunionCreee = reunionService.createWithProjet(reunion, projetId);
            return new ResponseEntity<>(reunionCreee, HttpStatus.CREATED);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> reunionExiste(@PathVariable Long id) {
        try {
            Optional<Reunion> reunion = reunionService.findById(id);
            return new ResponseEntity<>(reunion.isPresent(), HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }


}
