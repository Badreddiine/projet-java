package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.GroupeDTO;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.UserPrincipal;
import com.example.javaprojet.services.GroupeService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/groupes")
@RequiredArgsConstructor

public class GroupeController {

    private final GroupeService groupeService;

    /**
     * Rejoindre un groupe
     * @param idGroupe ID du groupe à rejoindre

     * @return ResponseEntity avec message de succès ou d'erreur
     */
    @PostMapping("/{idGroupe}/rejoindre")
    public ResponseEntity<String> rejoindreGroupe(
            @PathVariable Long idGroupe,
            @AuthenticationPrincipal UserPrincipal userPrincipal) {
        Long idUserConnecter = userPrincipal.getId();
        try {
            groupeService.rejoindreGroupe(idGroupe, idUserConnecter);
            return ResponseEntity.ok("Vous avez rejoint le groupe avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la tentative de rejoindre le groupe");
        }
    }

    /**
     * Créer un nouveau groupe
     * @param groupeDTO DTO contenant les informations du groupe
     * @param idUserConnecter ID de l'utilisateur connecté
     * @return ResponseEntity avec message de succès ou d'erreur
     */
    @PostMapping("/creer")
    public ResponseEntity<String> creationGroupe(
            @RequestBody GroupeDTO groupeDTO,
            @RequestParam Long idUserConnecter) {
        try {
            // Conversion du DTO vers l'entité (vous devrez adapter selon votre DTO)
            Groupe groupe = new Groupe();
            groupe.setNom(groupeDTO.getNom());
            groupe.setDescription(groupeDTO.getDescription());
            // Ajoutez d'autres propriétés selon votre DTO

            groupeService.creationGroupe(groupe, idUserConnecter);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body("Groupe créé avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la création du groupe");
        }
    }

    /**
     * Supprimer un groupe
     * @param idGroupe ID du groupe à supprimer
     * @param idUserConnecter ID de l'utilisateur connecté
     * @return ResponseEntity avec message de succès ou d'erreur
     */
    @DeleteMapping("/{idGroupe}")
    public ResponseEntity<String> supprimerGroupe(
            @PathVariable Long idGroupe,
            @RequestParam Long idUserConnecter) {
        try {
            groupeService.supprimerGroupe(idGroupe, idUserConnecter);
            return ResponseEntity.ok("Groupe supprimé avec succès");
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la suppression du groupe");
        }
    }

    /**
     * Obtenir un groupe par son ID
     * @param id ID du groupe
     * @return ResponseEntity contenant le groupe ou une erreur
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getGroupeById(@PathVariable Long id) {
        try {
            Groupe groupe = groupeService.findGroupeById(id);
            return ResponseEntity.ok(groupe);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la récupération du groupe");
        }
    }

    /**
     * Obtenir tous les groupes
     * @return ResponseEntity contenant la liste des groupes
     */
    @GetMapping
    public ResponseEntity<List<Groupe>> getAllGroupes() {
        try {
            List<Groupe> groupes = groupeService.getAllGroupes();
            return ResponseEntity.ok(groupes);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Vérifier si un groupe existe
     * @param id ID du groupe
     * @return ResponseEntity avec boolean indiquant l'existence
     */
    @GetMapping("/{id}/exists")
    public ResponseEntity<Boolean> groupeExists(@PathVariable Long id) {
        try {
            Optional<Groupe> groupe = groupeService.getGroupeById(id);
            return ResponseEntity.ok(groupe.isPresent());
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Sauvegarder un groupe (mise à jour)
     * @return ResponseEntity contenant le groupe sauvegardé
     */
    @PutMapping("/{id}")
    public ResponseEntity<?> saveGroupe(@PathVariable Long id, @RequestBody GroupeDTO groupeDTO) {
        try {
            Groupe groupe1 = groupeService.findGroupeById(id);
            if (groupe1 == null) {
                return ResponseEntity.notFound().build();
            }

            groupe1.setNom(groupeDTO.getNom());
            groupe1.setDescription(groupeDTO.getDescription());
            // Don't set the ID from DTO - it should remain the same as the path variable

            Groupe groupeSauvegarde = groupeService.saveGroupe(groupe1);
            return ResponseEntity.ok(groupeSauvegarde);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Erreur lors de la sauvegarde du groupe");
        }
    }
}
