package com.example.javaprojet.Controller;
import com.example.javaprojet.dto.TacheDTO;
import com.example.javaprojet.entity.Tache;
import com.example.javaprojet.entity.UserPrincipal;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.TacheService;
import com.example.javaprojet.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/taches")
@CrossOrigin(origins = "*")
public class TacheController {

    @Autowired
    private TacheService tacheService;

    @Autowired
    private UtilisateurService utilisateurService;

    /**
     * Créer une nouvelle tâche
     * @param tacheDTO les données de la tâche à créer
     * @return la tâche créée
     */
    @PostMapping
    public ResponseEntity<Tache> creerTache(@RequestBody TacheDTO tacheDTO) {
        try {
            Tache tache = new Tache(tacheDTO);
            Tache tacheCreee = tacheService.creerTache(tache);
            return new ResponseEntity<>(tacheCreee, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Mettre à jour une tâche existante
     * @param id l'ID de la tâche à modifier
     * @param tacheDTO les nouvelles données de la tâche
     * @return la tâche modifiée
     */
    @PutMapping("/{id}")
    public ResponseEntity<TacheDTO> mettreAJourTache(@PathVariable Long id, @RequestBody TacheDTO tacheDTO) {
        try {
            tacheDTO.setId(id); // S'assurer que l'ID correspond
            Tache tacheModifiee = tacheService.mettreAJourTache(tacheDTO);
            TacheDTO tacheDTO2 = new TacheDTO(tacheModifiee);
            return new ResponseEntity<>(tacheDTO2, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Supprimer une tâche
     * @param id l'ID de la tâche à supprimer
     * @return statut de la suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerTache(@PathVariable Long id) {
        try {
            tacheService.supprimerTache(id);
            return new ResponseEntity<>("Tâche supprimée avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la suppression", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer une tâche par son ID
     * @param id l'ID de la tâche
     * @return la tâche trouvée
     */
    @GetMapping("/{id}")
    public ResponseEntity<Tache> recupererTacheParId(@PathVariable Long id) {
        try {
            Tache tache = tacheService.recupererTacheParId(id);
            return new ResponseEntity<>(tache, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Récupérer toutes les tâches
     * @return liste de toutes les tâches
     */
    @GetMapping
    public ResponseEntity<List<TacheDTO>> recupererToutesLesTaches() {
        try {
            List<TacheDTO> taches = tacheService.recupererToutesLesTaches().stream()
                    .map(TacheDTO::new)
                    .collect(Collectors.toList());

            return new ResponseEntity<>(taches, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les tâches par état
     * @param etat l'état des tâches à récupérer
     * @return liste des tâches avec l'état spécifié
     */
    @GetMapping("/etat/{etat}")
    public ResponseEntity<List<Tache>> recupererTachesParEtat(@PathVariable String etat) {
        try {
            List<Tache> taches = tacheService.recupererTachesParEtat(etat);
            return new ResponseEntity<>(taches, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les tâches d'un utilisateur
     * @return liste des tâches assignées à l'utilisateur
     */
    @GetMapping("/utilisateur/{utilisateurId}")
    public ResponseEntity<List<Tache>> recupererTachesParUtilisateur( @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            List<Tache> taches = tacheService.recupererTachesParUtilisateur(userPrincipal.getId());
            return new ResponseEntity<>(taches, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Récupérer les tâches d'un projet
     * @param projetId l'ID du projet
     * @return liste des tâches du projet
     */
    @GetMapping("/projet/{projetId}")
    public ResponseEntity<List<Tache>> recupererTachesParProjet(@PathVariable Long projetId) {
        try {
            List<Tache> taches = tacheService.recupererTachesParProjet(projetId);
            return new ResponseEntity<>(taches, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Changer l'état d'une tâche
     * @param id l'ID de la tâche
     * @param nouvelEtat le nouvel état
     * @return la tâche avec l'état modifié
     */
    @PatchMapping("/{id}/etat")
    public ResponseEntity<Tache> changerEtat(@PathVariable Long id, @RequestBody String nouvelEtat) {
        try {
            Tache tache = tacheService.changerEtat(id, nouvelEtat);
            return new ResponseEntity<>(tache, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Assigner une tâche à un utilisateur
     * @param tacheId l'ID de la tâche
     * @param utilisateurId l'ID de l'utilisateur
     * @return la tâche assignée
     */
    @PatchMapping("/{tacheId}/assigner/{utilisateurId}")
    public ResponseEntity<Tache> prendreUneTache(@PathVariable Long tacheId, @PathVariable Long utilisateurId) {
        try {
            Utilisateur utilisateur = utilisateurService.getUtilisateurById(utilisateurId);
            Tache tache = tacheService.prendUneTache(tacheId, utilisateur);
            return new ResponseEntity<>(tache, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }





}