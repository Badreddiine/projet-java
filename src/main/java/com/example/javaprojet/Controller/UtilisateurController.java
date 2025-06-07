package com.example.javaprojet.Controller;


import com.example.javaprojet.dto.ProjetDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.UserPrincipal;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.services.UtilisateurService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/utilisateurs")
@CrossOrigin(origins = "*")
public class UtilisateurController {

    @Autowired
    private UtilisateurService utilisateurService;

    // ========== GESTION DES PROFILS ==========

    /**
     * Récupérer un utilisateur par ID
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur trouvé
     */
    @GetMapping("/{id}")
    public ResponseEntity<Utilisateur> recupererUtilisateurParId(@PathVariable Long id) {
        try {
            Utilisateur utilisateur = utilisateurService.getUtilisateurById(id);
            return new ResponseEntity<>(utilisateur, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Récupérer un utilisateur par email
     * @param email l'email de l'utilisateur
     * @return l'utilisateur trouvé
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<UtilisateurDTO> recupererUtilisateurParEmail(@PathVariable String email) {
        try {
            UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByEmail(email);
            return new ResponseEntity<>(utilisateur, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Récupérer tous les utilisateurs
     * @return liste de tous les utilisateurs
     */
    @GetMapping
    public ResponseEntity<List<UtilisateurDTO>> recupererTousLesUtilisateurs() {
        try {
            List<UtilisateurDTO> utilisateurs = utilisateurService.getAllUtilisateurs();
            return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Rechercher des utilisateurs par nom
     * @param nom le nom à rechercher
     * @return liste des utilisateurs correspondants
     */
    @GetMapping("/recherche")
    public ResponseEntity<List<UtilisateurDTO>> rechercherUtilisateursParNom(@RequestParam String nom) {
        try {
            List<UtilisateurDTO> utilisateurs = utilisateurService.rechercherUtilisateursParNom(nom);
            return new ResponseEntity<>(utilisateurs, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Modifier le profil d'un utilisateur
     * @param utilisateurDTO les nouvelles données
     * @return l'utilisateur modifié
     */
    @PutMapping("/{id}")
    public ResponseEntity<UtilisateurDTO> modifierProfil(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            UtilisateurDTO utilisateurModifie = utilisateurService.modifierProfil(userPrincipal.getId(), utilisateurDTO);
            return new ResponseEntity<>(utilisateurModifie, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Supprimer un compte utilisateur
     * @param id l'ID de l'utilisateur à supprimer
     * @return statut de la suppression
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimerCompte(@PathVariable Long id) {
        try {
            utilisateurService.supprimerCompte(id);
            return new ResponseEntity<>("Compte supprimé avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la suppression", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ========== GESTION DES MOTS DE PASSE ==========

    /**
     * Changer le mot de passe d'un utilisateur
     * @param id l'ID de l'utilisateur
     * @return statut du changement
     */
    @PatchMapping("/{id}/mot-de-passe")
    public ResponseEntity<String> changerMotDePasse(@PathVariable Long id, @RequestBody String motDePasse) {
        try {
            utilisateurService.changerMotDePasse(id, motDePasse);
            return new ResponseEntity<>("Mot de passe modifié avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Erreur lors de la modification du mot de passe", HttpStatus.BAD_REQUEST);
        }
    }

    // ========== GESTION DES RÔLES ==========

    /**
     * Changer le rôle d'un utilisateur
     * @param role  c est enum dans front end
     * @param id l'ID de l'utilisateur
     * @return l'utilisateur avec le nouveau rôle
     */
    @PatchMapping("/{id}/role")
    public ResponseEntity<UtilisateurDTO> changerRole(@PathVariable Long id, @RequestBody RoleType role) {
        try {
            Utilisateur Utilisateur = utilisateurService.getUtilisateurById(id);
            UtilisateurDTO utilisateurDTO = new UtilisateurDTO(Utilisateur);
            UtilisateurDTO utilisateurModifie = utilisateurService.changerRole(utilisateurDTO, role);
            return new ResponseEntity<>(utilisateurModifie, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
        }
    }


    // ========== GESTION DES PROJETS ==========

    /**
     * Récupérer les projets d'un utilisateur
     * @param id l'ID de l'utilisateur
     * @return liste des projets de l'utilisateur
     */
    @GetMapping("/{id}/projets")
    public ResponseEntity<List<ProjetDTO>> getProjetsByUtilisateur(@PathVariable Long id) {
        try {
            List<ProjetDTO> projets = utilisateurService.getProjetsByUtilisateur(id);
            return new ResponseEntity<>(projets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    /**
     * Récupérer les projets publics
     * @return liste des projets publics
     */
    @GetMapping("/projets/publics")
    public ResponseEntity<List<ProjetDTO>> getProjetsPuliques() {
        try {
            List<ProjetDTO> projets = utilisateurService.getProjetsPuliques();
            return new ResponseEntity<>(projets, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Demander à rejoindre un projet
     * @param utilisateurId l'ID de l'utilisateur
     * @param projetId l'ID du projet
     * @return statut de la demande
     */
    @PostMapping("/{utilisateurId}/projets/{projetId}/demander")
    public ResponseEntity<String> demanderRejoindreProjet(@PathVariable Long utilisateurId, @PathVariable Long projetId) {
        try {
            utilisateurService.demanderRejoindreProjet(utilisateurId, projetId);
            return new ResponseEntity<>("Demande envoyée avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Accepter une demande d'adhésion
     * @param utilisateurId l'ID de l'utilisateur a recuperer dans front
     * @param projetId l'ID du projet a recuperer dans front end
     * @return statut de l'acceptation
     */
    @PostMapping("/{utilisateurId}/projets/{projetId}/accepter")
    public ResponseEntity<String> accepterDemandeAdhesion(@PathVariable Long utilisateurId, @PathVariable Long projetId) {
        try {
            utilisateurService.accepterDemandeAdhesion(utilisateurId, projetId);
            return new ResponseEntity<>("Demande acceptée avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Rejeter une demande d'adhésion
     * @param utilisateurId l'ID de l'utilisateur
     * @param projetId l'ID du projet
     * @return statut du rejet
     */
    @PostMapping("/{utilisateurId}/projets/{projetId}/rejeter")
    public ResponseEntity<String> rejeterDemandeAdhesion(@PathVariable Long utilisateurId, @PathVariable Long projetId) {
        try {
            utilisateurService.rejeterDemandeAdhesion(utilisateurId, projetId);
            return new ResponseEntity<>("Demande rejetée avec succès", HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Récupérer les membres d'un projet
     * @param projetId l'ID du projet
     * @param utilisateurId l'ID de l'utilisateur demandeur
     * @return liste des membres du projet
     */
    @GetMapping("/projets/{projetId}/membres")
    public ResponseEntity<List<UtilisateurDTO>> getMembresDuProjet(@PathVariable Long projetId, @RequestParam Long utilisateurId) {
        try {
            List<UtilisateurDTO> membres = utilisateurService.getMembresDuProjet(projetId, utilisateurId);
            return new ResponseEntity<>(membres, HttpStatus.OK);
        } catch (SecurityException e) {
            return new ResponseEntity<>(null, HttpStatus.FORBIDDEN);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
        }
    }

    // ========== MÉTHODES DE VÉRIFICATION ==========

    /**
     * Vérifier si un utilisateur est membre d'un projet
     * @param utilisateurId l'ID de l'utilisateur
     * @param projetId l'ID du projet
     * @return true si l'utilisateur est membre
     */
    @GetMapping("/{utilisateurId}/projets/{projetId}/est-membre")
    public ResponseEntity<Boolean> estMembreDuProjet(@PathVariable Long utilisateurId, @PathVariable Long projetId) {
        try {
            boolean estMembre = utilisateurService.estMembreDuProjet(utilisateurId, projetId);
            return new ResponseEntity<>(estMembre, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vérifier si un utilisateur est admin d'un projet
     * @param utilisateurId l'ID de l'utilisateur
     * @param projetId l'ID du projet
     * @return true si l'utilisateur est admin
     */
    @GetMapping("/{utilisateurId}/projets/{projetId}/est-admin")
    public ResponseEntity<Boolean> estAdminDuProjet(@PathVariable Long utilisateurId, @PathVariable Long projetId) {
        try {
            boolean estAdmin = utilisateurService.estAdminDuProjet(utilisateurId, projetId);
            return new ResponseEntity<>(estAdmin, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * Vérifier si un utilisateur est l'admin principal d'un projet
     * @param utilisateurId l'ID de l'utilisateur
     * @param projetId l'ID du projet
     * @return true si l'utilisateur est l'admin principal
     */
    @GetMapping("/{utilisateurId}/projets/{projetId}/est-admin-principal")
    public ResponseEntity<Boolean> estAdminPrincipalDuProjet(@PathVariable Long utilisateurId, @PathVariable Long projetId) {
        try {
            boolean estAdminPrincipal = utilisateurService.estAdminPrincipalDuProjet(utilisateurId, projetId);
            return new ResponseEntity<>(estAdminPrincipal, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(false, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
