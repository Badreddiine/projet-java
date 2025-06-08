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
@CrossOrigin("*")
public class ReunionController {

    @Autowired
    private ReunionService reunionService;

    /**
     * Test de base pour vérifier que le contrôleur fonctionne
     */
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("ReunionController fonctionne !");
    }

    /**
     * Créer une nouvelle réunion avec un projet
     */
    @PostMapping("/creer")
    public ResponseEntity<Reunion> creerReunion(@RequestBody ReunionDTO reunionDTO) {
        try {
            System.out.println("=== DÉBUT CRÉATION RÉUNION ===");
            System.out.println("DTO reçu: " + reunionDTO);

            // Validation des données obligatoires
            if (reunionDTO.getIdProjet() == null) {
                System.out.println("Erreur: ID projet manquant");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            if (reunionDTO.getTitre() == null || reunionDTO.getTitre().trim().isEmpty()) {
                System.out.println("Erreur: Titre manquant");
                return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
            }

            System.out.println("Validation OK, création de l'entité Reunion...");

            // Création manuelle de l'entité plutôt qu'utiliser le constructeur
            Reunion reunion = new Reunion();
            reunion.setTitre(reunionDTO.getTitre());
            reunion.setDescription(reunionDTO.getDescription());
            reunion.setDate(reunionDTO.getDate());
            reunion.setLienMeet(reunionDTO.getLienMeet());
            reunion.setDuree(reunionDTO.getDuree());
            reunion.setEstObligatoire(reunionDTO.isEstObligatoire());

            System.out.println("Entité Reunion créée: " + reunion);
            System.out.println("Appel du service avec projetId: " + reunionDTO.getIdProjet());

            Reunion reunionCreee = reunionService.createWithProjet(reunion, reunionDTO.getIdProjet());

            System.out.println("Réunion créée avec succès: " + reunionCreee);
            return new ResponseEntity<>(reunionCreee, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            System.err.println("=== ERREUR RUNTIME ===");
            System.err.println("Message: " + e.getMessage());
            System.err.println("Cause: " + e.getCause());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("=== ERREUR GÉNÉRALE ===");
            System.err.println("Message: " + e.getMessage());
            System.err.println("Type: " + e.getClass().getSimpleName());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mettre à jour une réunion existante
     */
    @PutMapping("/{id}")
    public ResponseEntity<Reunion> mettreAJourReunion(@PathVariable Long id, @RequestBody ReunionDTO reunionDTO) {
        try {
            System.out.println("=== MISE À JOUR RÉUNION ===");
            System.out.println("ID: " + id);
            System.out.println("DTO: " + reunionDTO);

            reunionDTO.setId(id);
            Reunion reunionModifiee = reunionService.update(reunionDTO);
            return new ResponseEntity<>(reunionModifiee, HttpStatus.OK);

        } catch (RuntimeException e) {
            System.err.println("Erreur mise à jour RuntimeException: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Erreur générale mise à jour: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprimer une réunion
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerReunion(@PathVariable Long id) {
        try {
            System.out.println("=== SUPPRESSION RÉUNION ===");
            System.out.println("ID à supprimer: " + id);

            reunionService.delete(id);
            return new ResponseEntity<>("Réunion supprimée avec succès", HttpStatus.OK);

        } catch (RuntimeException e) {
            System.err.println("Erreur suppression: " + e.getMessage());
            return new ResponseEntity<>(e.getMessage(), HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Erreur générale suppression: " + e.getMessage());
            return new ResponseEntity<>("Erreur lors de la suppression", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer une réunion par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Reunion> recupererReunionParId(@PathVariable Long id) {
        try {
            System.out.println("=== RÉCUPÉRATION RÉUNION PAR ID ===");
            System.out.println("ID recherché: " + id);

            Optional<Reunion> reunion = reunionService.findById(id);
            if (reunion.isPresent()) {
                System.out.println("Réunion trouvée: " + reunion.get());
                return new ResponseEntity<>(reunion.get(), HttpStatus.OK);
            } else {
                System.out.println("Aucune réunion trouvée avec l'ID: " + id);
                return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
            }

        } catch (Exception e) {
            System.err.println("Erreur récupération par ID: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer toutes les réunions
     */
    @GetMapping
    public ResponseEntity<List<Reunion>> recupererToutesLesReunions() {
        try {
            System.out.println("=== RÉCUPÉRATION TOUTES LES RÉUNIONS ===");

            List<Reunion> reunions = reunionService.findAll();
            System.out.println("Nombre de réunions trouvées: " + reunions.size());

            return new ResponseEntity<>(reunions, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Erreur récupération toutes: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Ajouter un participant à une réunion (méthode admin)
     */
    @PostMapping("/{reunionId}/participants/{utilisateurId}")
    public ResponseEntity<Reunion> ajouterParticipant(@PathVariable Long reunionId, @PathVariable Long utilisateurId) {
        try {
            System.out.println("=== AJOUT PARTICIPANT (ADMIN) ===");
            System.out.println("Réunion ID: " + reunionId + ", Utilisateur ID: " + utilisateurId);

            Reunion reunion = reunionService.ajouterParticipant(reunionId, utilisateurId);
            return new ResponseEntity<>(reunion, HttpStatus.OK);

        } catch (RuntimeException e) {
            System.err.println("Erreur ajout participant: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Erreur générale ajout participant: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Participer à une réunion (utilisateur connecté)
     */
    @PostMapping("/{reunionId}/participants")
    public ResponseEntity<Reunion> participerAReunion(@PathVariable Long reunionId, @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            System.out.println("=== PARTICIPATION UTILISATEUR ===");
            System.out.println("Réunion ID: " + reunionId);

            if (userPrincipal == null) {
                System.out.println("Erreur: Utilisateur non authentifié");
                return new ResponseEntity<>(null, HttpStatus.UNAUTHORIZED);
            }

            System.out.println("Utilisateur ID: " + userPrincipal.getId());

            Reunion reunion = reunionService.ajouterParticipant(reunionId, userPrincipal.getId());
            return new ResponseEntity<>(reunion, HttpStatus.OK);

        } catch (RuntimeException e) {
            System.err.println("Erreur participation: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Erreur générale participation: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Créer une réunion pour un projet spécifique
     */
    @PostMapping("/projet/{projetId}")
    public ResponseEntity<Reunion> creerReunionPourProjet(@PathVariable Long projetId, @RequestBody ReunionDTO reunionDTO) {
        try {
            System.out.println("=== CRÉATION RÉUNION POUR PROJET ===");
            System.out.println("Projet ID: " + projetId);
            System.out.println("DTO: " + reunionDTO);

            // Création manuelle de l'entité
            Reunion reunion = new Reunion();
            reunion.setTitre(reunionDTO.getTitre());
            reunion.setDescription(reunionDTO.getDescription());
            reunion.setDate(reunionDTO.getDate());
            reunion.setLienMeet(reunionDTO.getLienMeet());
            reunion.setDuree(reunionDTO.getDuree());
            reunion.setEstObligatoire(reunionDTO.isEstObligatoire());

            Reunion reunionCreee = reunionService.createWithProjet(reunion, projetId);
            return new ResponseEntity<>(reunionCreee, HttpStatus.CREATED);

        } catch (RuntimeException e) {
            System.err.println("Erreur création pour projet: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            System.err.println("Erreur générale création pour projet: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vérifier si une réunion existe
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> reunionExiste(@PathVariable Long id) {
        try {
            System.out.println("=== VÉRIFICATION EXISTENCE ===");
            System.out.println("ID à vérifier: " + id);

            Optional<Reunion> reunion = reunionService.findById(id);
            boolean exists = reunion.isPresent();

            System.out.println("Réunion existe: " + exists);
            return new ResponseEntity<>(exists, HttpStatus.OK);

        } catch (Exception e) {
            System.err.println("Erreur vérification existence: " + e.getMessage());
            e.printStackTrace();
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Endpoint de debug pour voir les données reçues
     */
    @PostMapping("/debug")
    public ResponseEntity<String> debugReunion(@RequestBody ReunionDTO reunionDTO) {
        System.out.println("=== DEBUG RÉUNION ===");
        System.out.println("Titre: " + reunionDTO.getTitre());
        System.out.println("Description: " + reunionDTO.getDescription());
        System.out.println("Date: " + reunionDTO.getDate());
        System.out.println("Lien Meet: " + reunionDTO.getLienMeet());
        System.out.println("Durée: " + reunionDTO.getDuree());
        System.out.println("ID Projet: " + reunionDTO.getIdProjet());
        System.out.println("Est Obligatoire: " + reunionDTO.isEstObligatoire());

        return ResponseEntity.ok("Debug OK - Vérifiez la console");
    }
}