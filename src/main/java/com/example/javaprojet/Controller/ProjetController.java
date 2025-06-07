package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.GroupeDTO;
import com.example.javaprojet.dto.ProjetDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.UserPrincipal;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.StatutProjet;
import com.example.javaprojet.services.ProjetService;
import com.example.javaprojet.services.UtilisateurService;
import com.example.javaprojet.services.GroupeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/projets")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "*")
public class ProjetController {

    private final ProjetService projetService;
    private final UtilisateurService utilisateurService;
    private final GroupeService groupeService;

    // ========== GESTION DES PROJETS ==========

    @PostMapping("/creer")
    public ResponseEntity<?> creerProjet(@Valid @RequestBody ProjetDTO projetDTO,
                                         @AuthenticationPrincipal UserPrincipal userPrincipal,
                                         @RequestParam Long groupeId) {
        try {
            Groupe groupe = groupeService.getById(groupeId);
            if (groupe == null) {
                return ResponseEntity.badRequest().body("Groupe introuvable");
            }

            Projet nouveauProjet = projetService.creerProjet(projetDTO, userPrincipal.getId(), groupe);
            return ResponseEntity.ok(new ProjetDTO(nouveauProjet));
        } catch (Exception e) {
            log.error("Erreur création projet", e);
            return ResponseEntity.internalServerError().body("Erreur création projet");
        }
    }

    @PutMapping("/modifier")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> mettreAJourProjet(@Valid @RequestBody ProjetDTO projetDTO,
                                               @AuthenticationPrincipal UserPrincipal userPrincipal) {
        try {
            Projet projetModifie = projetService.mettreAJourProjet(projetDTO, userPrincipal.getId());
            ProjetDTO resultat = new ProjetDTO(projetModifie);

            return ResponseEntity.ok(resultat);
        } catch (Exception e) {
            log.error("Erreur lors de la modification du projet: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la modification: " + e.getMessage());
        }
    }

    @DeleteMapping("/supprimer/{projetId}")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> supprimerProjet(@PathVariable Long projetId,
                                             @RequestParam Long utilisateurId) {
        try {
            projetService.supprimerProjet(projetId, utilisateurId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du projet: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la suppression: " + e.getMessage());
        }
    }

    // ========== GESTION DES STATUTS ==========

    /**
     *
     * @param projetId a recuperer dans fronnt
     * @param adminId a recuperer dans front
     * @return
     */
    @PutMapping("/accepter")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> accepterProjet(@RequestParam Long projetId,
                                            @RequestParam Long adminId) {
        try {
            projetService.accepterProjet(projetId, adminId);
            return ResponseEntity.ok("Projet accepté avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'acceptation du projet: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de l'acceptation: " + e.getMessage());
        }
    }

    @PutMapping("/rejeter")
    @Secured("ROLE_ADMIN")
    public ResponseEntity<?> rejeterProjet(@RequestParam Long projetId,
                                           @RequestParam Long adminId) {
        try {
            projetService.rejeterProjet(projetId, adminId);
            return ResponseEntity.ok("Projet rejeté avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du rejet du projet: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors du rejet: " + e.getMessage());
        }
    }

    @PutMapping("/cloturer")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> cloturerProjet(@RequestParam Long projetId,
                                            @RequestParam Long utilisateurId) {
        try {
            projetService.cloturerProjet(projetId, utilisateurId);
            return ResponseEntity.ok("Projet clôturé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la clôture du projet: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la clôture: " + e.getMessage());
        }
    }

    @PutMapping("/reactiver")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> reactiverProjet(@RequestParam Long projetId,
                                             @RequestParam Long utilisateurId) {
        try {
            projetService.reactiverProjet(projetId, utilisateurId);
            return ResponseEntity.ok("Projet réactivé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la réactivation du projet: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la réactivation: " + e.getMessage());
        }
    }

    // ========== GESTION DES MEMBRES ==========

    @PostMapping("/membres/ajouter")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> ajouterMembre(@RequestParam Long projetId,
                                           @RequestParam Long utilisateurId,
                                           @RequestParam Long adminId) {
        try {
            projetService.ajouterMembre(projetId, utilisateurId, adminId);
            return ResponseEntity.ok("Membre ajouté avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'ajout du membre: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de l'ajout du membre: " + e.getMessage());
        }
    }

    @DeleteMapping("/membres/supprimer")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> supprimerMembre(@RequestParam Long projetId,
                                             @RequestParam Long utilisateurId,
                                             @RequestParam Long adminId) {
        try {
            projetService.supprimerMembre(projetId, utilisateurId, adminId);
            return ResponseEntity.ok("Membre supprimé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la suppression du membre: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la suppression du membre: " + e.getMessage());
        }
    }

    // ========== GESTION DES DEMANDES D'ADHÉSION ==========

    @PostMapping("/demandes/accepter")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> accepterDemande(@RequestParam Long projetId,
                                             @RequestParam Long demandeurId,
                                             @RequestParam Long adminId) {
        try {
            projetService.accepterDemande(projetId, demandeurId, adminId);
            return ResponseEntity.ok("Demande acceptée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de l'acceptation de la demande: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de l'acceptation de la demande: " + e.getMessage());
        }
    }

    @PostMapping("/demandes/refuser")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> refuserDemande(@RequestParam Long projetId,
                                            @RequestParam Long demandeurId,
                                            @RequestParam Long adminId) {
        try {
            projetService.refuserDemande(projetId, demandeurId, adminId);
            return ResponseEntity.ok("Demande refusée avec succès");
        } catch (Exception e) {
            log.error("Erreur lors du refus de la demande: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors du refus de la demande: " + e.getMessage());
        }
    }

    // ========== GESTION DES ADMINISTRATEURS ==========

    @PostMapping("/admins/promouvoir")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> promouvoirEnAdmin(@RequestParam Long projetId,
                                               @RequestParam Long utilisateurId,
                                               @RequestParam Long adminId) {
        try {
            projetService.promouvoirEnAdmin(projetId, utilisateurId, adminId);
            return ResponseEntity.ok("Utilisateur promu administrateur avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la promotion: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la promotion: " + e.getMessage());
        }
    }

    @PostMapping("/admins/retrograder")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> retrograderAdmin(@RequestParam Long projetId,
                                              @RequestParam Long utilisateurId,
                                              @RequestParam Long adminId) {
        try {
            projetService.retrograderAdmin(projetId, utilisateurId, adminId);
            return ResponseEntity.ok("Administrateur rétrogradé avec succès");
        } catch (Exception e) {
            log.error("Erreur lors de la rétrogradation: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la rétrogradation: " + e.getMessage());
        }
    }

    // ========== MÉTHODES DE CONSULTATION ==========

    @GetMapping("/liste")
    public ResponseEntity<List<ProjetDTO>> getAllProjets() {
        try {
            List<ProjetDTO> projets = projetService.getAllProjets();
            return ResponseEntity.ok(projets);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des projets: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getProjetById(@PathVariable Long id) {
        try {
            Projet projet = projetService.findProjetById(id);
            ProjetDTO projetDTO = new ProjetDTO(projet);
            return ResponseEntity.ok(projetDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération du projet: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Projet non trouvé: " + e.getMessage());
        }
    }

    @GetMapping("/publics")
    public ResponseEntity<List<ProjetDTO>> getProjetsPublics() {
        try {
            List<Projet> projets = projetService.getProjetsPublics();
            List<ProjetDTO> projetsDTO = projets.stream()
                    .map(ProjetDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(projetsDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des projets publics: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/statut/{statut}")
    public ResponseEntity<List<ProjetDTO>> getProjetsByStatut(@PathVariable String statut) {
        try {
            StatutProjet statutProjet = StatutProjet.valueOf(statut.toUpperCase());
            List<Projet> projets = projetService.getProjetsByStatut(statutProjet);
            List<ProjetDTO> projetsDTO = projets.stream()
                    .map(ProjetDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(projetsDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des projets par statut: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/theme/{theme}")
    public ResponseEntity<List<ProjetDTO>> getProjetsByTheme(@PathVariable String theme) {
        try {
            List<Projet> projets = projetService.getProjetsByTheme(theme);
            List<ProjetDTO> projetsDTO = projets.stream()
                    .map(ProjetDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(projetsDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des projets par thème: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/rechercher")
    public ResponseEntity<List<ProjetDTO>> rechercherProjets(@RequestParam String motCle) {
        try {
            List<Projet> projets = projetService.rechercherProjets(motCle);
            List<ProjetDTO> projetsDTO = projets.stream()
                    .map(ProjetDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(projetsDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la recherche de projets: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/{id}/membres")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN", "ROLE_MEMBRE"})
    public ResponseEntity<?> getMembres(@PathVariable Long id) {
        try {
            Set<Utilisateur> membres = projetService.getMembres(id);
            Set<UtilisateurDTO> membresDTO = membres.stream()
                    .map(UtilisateurDTO::new)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(membresDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des membres: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la récupération des membres: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/admins")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN", "ROLE_MEMBRE"})
    public ResponseEntity<?> getAdmins(@PathVariable Long id) {
        try {
            Set<Utilisateur> admins = projetService.getAdmins(id);
            Set<UtilisateurDTO> adminsDTO = admins.stream()
                    .map(UtilisateurDTO::new)
                    .collect(Collectors.toSet());
            return ResponseEntity.ok(adminsDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des admins: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la récupération des admins: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/demandes-en-attente")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> getDemandesEnAttente(@PathVariable Long id,
                                                  @RequestParam Long adminId) {
        try {
            List<Utilisateur> demandeurs = projetService.getDemandesEnAttente(id, adminId);
            List<UtilisateurDTO> demandeursDTO = demandeurs.stream()
                    .map(UtilisateurDTO::new)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(demandeursDTO);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des demandes: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la récupération des demandes: " + e.getMessage());
        }
    }

    @GetMapping("/{id}/statistiques")
    @Secured({"ROLE_ADMIN_PROJET", "ROLE_ADMIN"})
    public ResponseEntity<?> getStatistiquesProjet(@PathVariable Long id) {
        try {
            Map<String, Object> statistiques = projetService.getStatistiquesProjet(id);
            return ResponseEntity.ok(statistiques);
        } catch (Exception e) {
            log.error("Erreur lors de la récupération des statistiques: {}", e.getMessage());
            return ResponseEntity.badRequest().body("Erreur lors de la récupération des statistiques: " + e.getMessage());
        }
    }

    // ========== MÉTHODES DE VÉRIFICATION ==========

    @GetMapping("/peut-voir")
    public ResponseEntity<Boolean> peutVoirProjet(@RequestParam Long projetId,
                                                  @RequestParam(required = false) Long utilisateurId) {
        try {
            boolean peutVoir = projetService.peutVoirProjet(projetId, utilisateurId);
            return ResponseEntity.ok(peutVoir);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de visibilité: {}", e.getMessage());
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/est-membre")
    public ResponseEntity<Boolean> estMembreProjet(@RequestParam Long projetId,
                                                   @RequestParam Long utilisateurId) {
        try {
            boolean estMembre = projetService.estMembreProjet(projetId, utilisateurId);
            return ResponseEntity.ok(estMembre);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification de membership: {}", e.getMessage());
            return ResponseEntity.ok(false);
        }
    }

    @GetMapping("/est-admin")
    public ResponseEntity<Boolean> estAdminProjet(@RequestParam Long projetId,
                                                  @RequestParam Long utilisateurId) {
        try {
            boolean estAdmin = projetService.estAdminProjet(projetId, utilisateurId);
            return ResponseEntity.ok(estAdmin);
        } catch (Exception e) {
            log.error("Erreur lors de la vérification d'administration: {}", e.getMessage());
            return ResponseEntity.ok(false);
        }
    }
}