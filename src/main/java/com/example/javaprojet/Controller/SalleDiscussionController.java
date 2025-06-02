package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.SalleDiscussionDTO;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.GroupeService;
import com.example.javaprojet.services.ProjetService;
import com.example.javaprojet.services.SalleDiscussionService;
import com.example.javaprojet.services.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salles-discussion")
@RequiredArgsConstructor
public class SalleDiscussionController {

    private final SalleDiscussionService salleDiscussionService;
    private final GroupeService groupeService;
    private final ProjetService projetService;
    private final UtilisateurService utilisateurService;

    /**
     * Récupérer une salle de discussion par son ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<SalleDiscussionDTO> getSalleById(@PathVariable Long id) {
        Optional<SalleDiscussion> salle = salleDiscussionService.getSalleById(id);
        return salle.map(s -> ResponseEntity.ok(new SalleDiscussionDTO(s)))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Créer une salle de discussion pour un groupe
     */
    @PostMapping("/groupe/{groupeId}")
    @Secured("ROLE_USER")
    public ResponseEntity<SalleDiscussionDTO> creerSalleGroupe(
            @PathVariable Long groupeId,
            @RequestParam Long createurId,
            @RequestBody @Valid SalleDiscussionDTO salleDiscussionDTO) {

        try {
            // Récupération des entités depuis les services
            Groupe groupe = groupeService.findGroupeById(groupeId);
            Utilisateur createur = utilisateurService.findUtilisateurById(createurId);

            SalleDiscussion salle = salleDiscussionService.creerSalleGroupe(
                    salleDiscussionDTO, groupe, createur);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SalleDiscussionDTO(salle));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Créer une salle de discussion pour un projet
     */
    @PostMapping("/projet/{projetId}")
    @Secured("ROLE_USER")
    public ResponseEntity<SalleDiscussionDTO> creerSalleProjet(
            @PathVariable Long projetId,
            @RequestParam Long createurId,
            @RequestBody @Valid SalleDiscussionDTO salleDiscussionDTO) {

        try {
            // Récupération des entités depuis les services
            Projet projet = projetService.findProjetById(projetId);
            Utilisateur createur = utilisateurService.findUtilisateurById(createurId);

            SalleDiscussion salle = salleDiscussionService.creerSalleProjet(
                    salleDiscussionDTO, projet, createur);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SalleDiscussionDTO(salle));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Créer une salle de discussion générale
     */
    @PostMapping("/generale")
    @Secured("ROLE_USER")
    public ResponseEntity<SalleDiscussionDTO> creerSalleGenerale(
            @RequestParam Long createurId,
            @RequestBody @Valid SalleDiscussionDTO salleDiscussionDTO) {

        try {
            Utilisateur createur = utilisateurService.findUtilisateurById(createurId);

            SalleDiscussion salle = salleDiscussionService.creerSalleGenerale(
                    salleDiscussionDTO, createur);

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new SalleDiscussionDTO(salle));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Créer une session privée entre deux utilisateurs
     */
    @PostMapping("/privee")
    @Secured("ROLE_USER")
    public ResponseEntity<SalleDiscussionDTO> creerSessionPrivee(
            @RequestParam Long utilisateur1Id,
            @RequestParam Long utilisateur2Id) {

        try {
            Utilisateur utilisateur1 = utilisateurService.findUtilisateurById(utilisateur1Id);
            Utilisateur utilisateur2 = utilisateurService.findUtilisateurById(utilisateur2Id);

            SalleDiscussion salle = salleDiscussionService.creerSessionPrivee(
                    utilisateur1, utilisateur2);

            return ResponseEntity.ok(new SalleDiscussionDTO(salle));
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupérer toutes les salles d'un utilisateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<SalleDiscussionDTO>> getSallesUtilisateur(@PathVariable Long utilisateurId) {
        try {
            Utilisateur utilisateur = utilisateurService.findUtilisateurById(utilisateurId);

            List<SalleDiscussion> salles = salleDiscussionService.getSallesUtilisateur(utilisateur);
            List<SalleDiscussionDTO> sallesDTO = salles.stream()
                    .map(SalleDiscussionDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(sallesDTO);
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupérer les salles d'un projet
     */
    @GetMapping("/projet/{projetId}/salles")
    public ResponseEntity<List<SalleDiscussionDTO>> getSallesProjet(@PathVariable Long projetId) {
        try {
            List<SalleDiscussion> salles = salleDiscussionService.getSallesProjet(projetId);
            List<SalleDiscussionDTO> sallesDTO = salles.stream()
                    .map(SalleDiscussionDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(sallesDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Récupérer les salles d'un groupe
     */
    @GetMapping("/groupe/{groupeId}/salles")
    public ResponseEntity<List<SalleDiscussionDTO>> getSallesGroupe(@PathVariable Long groupeId) {
        try {
            List<SalleDiscussion> salles = salleDiscussionService.getSallesGroupe(groupeId);
            List<SalleDiscussionDTO> sallesDTO = salles.stream()
                    .map(SalleDiscussionDTO::new)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(sallesDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Ajouter un utilisateur à une salle
     */
    @PostMapping("/{salleId}/membres/{utilisateurId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> ajouterUtilisateur(
            @PathVariable Long salleId,
            @PathVariable Long utilisateurId) {

        try {
            Optional<SalleDiscussion> salleOpt = salleDiscussionService.getSalleById(salleId);
            if (salleOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Utilisateur utilisateur = utilisateurService.findUtilisateurById(utilisateurId);

            salleDiscussionService.ajouterUtilisateur(salleOpt.get(), utilisateur);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprimer un utilisateur d'une salle
     */
    @DeleteMapping("/{salleId}/membres/{utilisateurId}")
    @Secured("ROLE_USER")
    public ResponseEntity<Void> supprimerUtilisateur(
            @PathVariable Long salleId,
            @PathVariable Long utilisateurId) {

        try {
            Optional<SalleDiscussion> salleOpt = salleDiscussionService.getSalleById(salleId);
            if (salleOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Utilisateur utilisateur = utilisateurService.findUtilisateurById(utilisateurId);

            salleDiscussionService.supprimerUtilisateur(salleOpt.get(), utilisateur);
            return ResponseEntity.ok().build();
        } catch (EntityNotFoundException e) {
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Supprimer une salle de discussion
     */
    @DeleteMapping("/{salleId}")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<Void> supprimerSalle(@PathVariable Long salleId) {
        try {
            salleDiscussionService.supprimerSalle(salleId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }
}