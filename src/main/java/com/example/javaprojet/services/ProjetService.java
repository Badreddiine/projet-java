package com.example.javaprojet.services;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.example.javaprojet.model.RoleType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.ProjetRole;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.RoleSecondaire;
import com.example.javaprojet.model.StatutProjet;
import com.example.javaprojet.repo.GroupeRepesitory;
import com.example.javaprojet.repo.ProjetRepesitory;
import com.example.javaprojet.repo.ProjetRoleRepository;
import com.example.javaprojet.repo.UtilisateurRepesitory;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;

/**
 * Service gérant les opérations liées aux projets
 */
@Service
public class ProjetService {
    private static final String ERREUR_PROJET_NON_TROUVE = "Projet non trouvé";
    private static final String ERREUR_ADMIN_NON_TROUVE = "Administrateur non trouvé";
    private static final String ERREUR_UTILISATEUR_NON_TROUVE = "Utilisateur non trouvé";
    private static final String ERREUR_PROJET_DEJA_ACCEPTE = "Le projet a déjà été accepté";
    private static final String ERREUR_PROJET_DEJA_REFUSE = "Le projet a déjà été refusé";
    private static final String ERREUR_AUCUN_GROUPE = "Aucun groupe associé";
    private static final String ERREUR_UTILISATEUR_NON_MEMBRE = "L'utilisateur ne fait pas partie de ce projet";
    private static final String ERREUR_AUCUNE_DEMANDE = "Aucune demande trouvée pour cet utilisateur";
    private static final String ERREUR_ADMIN = "Seuls les administrateurs de projet peuvent effectuer cette opération ";
    private static final String ERREUR_ADMIN_PROJET="seul l'admin que peut effectuer cette operation";

    @Autowired
    private ProjetRepesitory projetRepesitory;
    @Autowired
    private GroupeRepesitory groupeRepesitory;
    @Autowired
    private ProjetRoleRepository projetRoleRepository;
    @Autowired
    private UtilisateurRepesitory utilisateurRepesitory;

    /**
     * Accepte un projet
     * @param idProjet ID du projet à accepter
     * @param idUserConnecter ID de l'utilisateur connecté
     * @throws EntityNotFoundException si le projet ou l'administrateur n'est pas trouvé
     * @throws IllegalStateException si l'utilisateur n'a pas les droits ou si le projet est déjà accepté/refusé
     */
    @Transactional
    public void accepterProjet(Long idProjet, Long idUserConnecter) {
        if (idProjet == null || idUserConnecter == null) {
            throw new IllegalArgumentException("Les IDs ne peuvent pas être null");
        }

        Projet projet = projetRepesitory.findById(idProjet)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_PROJET_NON_TROUVE));

        Utilisateur admin = utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_ADMIN_NON_TROUVE));

        if(!admin.getRoleSecondaire().equals(RoleType.ADMIN)){
            throw new IllegalStateException(ERREUR_ADMIN);
        }

        if (StatutProjet.REFUSER.equals(projet.getStatutProjet())) {
            throw new IllegalStateException(ERREUR_PROJET_DEJA_REFUSE);
        }
        if (StatutProjet.ACCEPTER.equals(projet.getStatutProjet())) {
            throw new IllegalStateException(ERREUR_PROJET_DEJA_ACCEPTE);
        }

        projet.setStatutProjet(StatutProjet.ACCEPTER);
        projet.setDateAcceptation(new Date());

        Groupe groupe = projet.getGroupe();
        if (groupe == null) {
            throw new IllegalStateException(ERREUR_AUCUN_GROUPE);
        }

        if (!groupe.getProjets().contains(projet)) {
            groupe.getProjets().add(projet);
            groupeRepesitory.save(groupe);
        }

        projetRepesitory.save(projet);
    }
    @Transactional
    public void rejeterProjet(Long projetId, Long idUserConnecter) {
        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_PROJET_NON_TROUVE ));

        Utilisateur admin = utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_ADMIN_NON_TROUVE));

        if(!admin.getRoleSecondaire().equals(RoleType.ADMIN)){
            throw new IllegalStateException(ERREUR_ADMIN);
        }

        if (StatutProjet.REFUSER.equals(projet.getStatutProjet())) {
            throw new IllegalStateException(ERREUR_PROJET_DEJA_REFUSE);
        }
        if (StatutProjet.ACCEPTER.equals(projet.getStatutProjet())) {
            throw new IllegalStateException(ERREUR_PROJET_DEJA_ACCEPTE);
        }

        projet.setStatutProjet(StatutProjet.REFUSER);
        projet.setDateRejet(new Date());

        Groupe groupe = projet.getGroupe();
        if (groupe != null) {
            groupe.getProjets().remove(projet);
            groupeRepesitory.save(groupe);
        }

        projetRepesitory.save(projet);
    }

    @Transactional
    public void supprimerProjet(Long projetId, Long adminId) {
        Utilisateur admin = utilisateurRepesitory.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_PROJET_NON_TROUVE));

        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_PROJET_NON_TROUVE));

       if(admin.getRoleSecondaire().equals(RoleSecondaire.ADMIN_PROJET)){
           projetRoleRepository.deleteByProjetId(projetId);
           projetRepesitory.delete(projet);
       }
    }
    /**
     * Supprime un membre d'un projet
     * @param utilisateurId ID de l'utilisateur à supprimer
     * @param adminId ID de l'administrateur effectuant l'opération
     * @param projetId ID du projet
     * @throws EntityNotFoundException si le projet, l'administrateur ou l'utilisateur n'est pas trouvé
     * @throws IllegalStateException si l'administrateur n'a pas les droits
     */
    @Transactional
    public void supprimerMembre(Long utilisateurId, Long adminId, Long projetId) {
        if (utilisateurId == null || adminId == null || projetId == null) {
            throw new IllegalArgumentException("Les IDs ne peuvent pas être null");
        }

        Utilisateur admin = utilisateurRepesitory.findById(adminId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_ADMIN_NON_TROUVE));

        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_PROJET_NON_TROUVE));

        Utilisateur utilisateur = utilisateurRepesitory.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_UTILISATEUR_NON_TROUVE));

        if(!admin.getRoleSecondaire().equals(RoleSecondaire.ADMIN_PROJET)){
            throw new IllegalStateException(ERREUR_ADMIN_PROJET);
        }

        if (!projet.getMembres().contains(utilisateur)) {
            throw new IllegalStateException(ERREUR_UTILISATEUR_NON_MEMBRE);
        }

        projet.getMembres().remove(utilisateur);
        utilisateur.getProjets().remove(projet);

        projetRoleRepository.deleteByProjetAndUtilisateur(projet, utilisateur);
        projetRepesitory.save(projet);
        utilisateurRepesitory.save(utilisateur);
    }

    @Transactional
    public void supprimerUtilisateurDuProjet(Long projetId, Long utilisateurId, Long idUserConnecter) {

        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

        Utilisateur admin = utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException("Admin non trouvé"));

        Utilisateur utilisateur = utilisateurRepesitory.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (!projet.getMembres().contains(utilisateur)) {
            throw new IllegalStateException("L'utilisateur ne fait pas partie de ce projet");
        }
        if (admin.getRoleSecondaire().equals(RoleSecondaire.ADMIN_PROJET)) {
            throw new IllegalStateException("vous etes pas un admin");
        }

        projetRoleRepository.deleteByProjetAndUtilisateur(projet, utilisateur);
        projet.getMembres().remove(utilisateur);
        utilisateur.getProjets().remove(projet);

        projetRepesitory.save(projet);
        utilisateurRepesitory.save(utilisateur);
    }

    @Transactional
    public void accepterDemande(Long projetId, Long demandeurId, Long idUserConnecter) {
        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_PROJET_NON_TROUVE));

        Utilisateur demandeur = utilisateurRepesitory.findById(demandeurId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_UTILISATEUR_NON_TROUVE));

        Utilisateur admin = utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_UTILISATEUR_NON_TROUVE));

        if(!admin.getRoleSecondaire().equals(RoleSecondaire.ADMIN_PROJET)){
            throw new IllegalStateException(ERREUR_ADMIN);
        }

        if (!projet.getDemandeursEnAttente().remove(demandeur)) {
            throw new IllegalStateException("Aucune demande trouvée pour cet utilisateur");
        }

        demandeur.setRoleSecondaire(RoleSecondaire.MEMBRE_PTOJET);
        projet.getMembres().add(demandeur);
        projetRoleRepository.save(new ProjetRole(projet, demandeur, RoleSecondaire.MEMBRE_PTOJET));
        projetRepesitory.save(projet);
    }

    @Transactional
    public void refuserDemande(Long projetId, Long demandeurId, Long idUserConnecter) {
        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_PROJET_NON_TROUVE));

        Utilisateur admin = utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_UTILISATEUR_NON_TROUVE));

        Utilisateur demandeur = utilisateurRepesitory.findById(demandeurId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_UTILISATEUR_NON_TROUVE));

        if(!admin.getRoleSecondaire().equals(RoleSecondaire.ADMIN_PROJET)){
            throw new IllegalStateException(ERREUR_ADMIN);
        }

        if (!projet.getDemandeursEnAttente().remove(demandeur)) {
            throw new IllegalStateException("Aucune demande trouvée");
        }

        projetRepesitory.save(projet);
    }

    /**
     * Affiche la liste des demandes pour rejoindre un projet
     * @param projetId ID du projet
     * @param demandeurId ID du demandeur
     * @param idUserConnecter ID de l'utilisateur connecté
     * @throws EntityNotFoundException si le projet ou l'administrateur n'est pas trouvé
     * @throws IllegalStateException si l'administrateur n'a pas les droits
     */
    public List<Utilisateur> afficherListDemandeRejoindreProjet(Long projetId, Long demandeurId, Long idUserConnecter) {
        if (projetId == null || demandeurId == null || idUserConnecter == null) {
            throw new IllegalArgumentException("Les IDs ne peuvent pas être null");
        }

        Projet projet = projetRepesitory.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_PROJET_NON_TROUVE));

        Utilisateur admin = utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException(ERREUR_ADMIN_NON_TROUVE));

        if(!admin.getRoleSecondaire().equals(RoleSecondaire.ADMIN_PROJET)){
            throw new IllegalStateException(ERREUR_ADMIN_PROJET);
        }

        return new ArrayList<>(projet.getDemandeursEnAttente());
    }

}

