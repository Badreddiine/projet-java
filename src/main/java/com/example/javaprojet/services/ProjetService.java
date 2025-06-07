package com.example.javaprojet.services;

import com.example.javaprojet.dto.ProjetDTO;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.ProjetRole;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.RoleSecondaire;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.enums.StatutProjet;
import com.example.javaprojet.repo.GroupeRepository;
import com.example.javaprojet.repo.ProjetRepesitory;
import com.example.javaprojet.repo.ProjetRoleRepository;
import com.example.javaprojet.repo.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProjetService {

    // Messages d'erreur constants
    private static final String ERREUR_PROJET_NON_TROUVE = "Projet non trouvé avec l'ID: %d";
    private static final String ERREUR_UTILISATEUR_NON_TROUVE = "Utilisateur non trouvé avec l'ID: %d";
    private static final String ERREUR_PROJET_DEJA_ACCEPTE = "Le projet a déjà été accepté";
    private static final String ERREUR_PROJET_DEJA_REFUSE = "Le projet a déjà été refusé";
    private static final String ERREUR_UTILISATEUR_NON_MEMBRE = "L'utilisateur ne fait pas partie de ce projet";
    private static final String ERREUR_AUCUNE_DEMANDE = "Aucune demande trouvée pour cet utilisateur";
    private static final String ERREUR_PERMISSION_REFUSEE = "Permissions insuffisantes pour effectuer cette opération";
    private static final String ERREUR_ADMIN_PRINCIPAL_PROTECTION = "L'administrateur principal ne peut pas être supprimé ou rétrogradé";
    private static final String ERREUR_UTILISATEUR_DEJA_MEMBRE = "L'utilisateur est déjà membre du projet";
    private static final String ERREUR_SEULS_PROJETS_ACCEPTES_PEUVENT_ETRE_CLOTURES = "Seuls les projets acceptés peuvent être clôturés";
    private static final String ERREUR_SEULS_PROJETS_CLOTURES_PEUVENT_ETRE_REACTIVES = "Seuls les projets clôturés peuvent être réactivés";

    // Repositories
    private final ProjetRepesitory projetRepesitory;
    private final UtilisateurRepository utilisateurRepository;
    private final GroupeRepository groupeRepository;
    private final ProjetRoleRepository projetRoleRepository;

    // ========== GESTION DES PROJETS ==========

    /**
     * creer projet
     * @param projetDTO vient du controlleur
     *
     * @return
   */
    @Transactional
    public Projet creerProjet(ProjetDTO projetDTO, Long id, Groupe groupe) {
        // Validation
        if (projetDTO == null || groupe == null) {
            throw new IllegalArgumentException("Paramètres invalides");
        }

        // Charge l'utilisateur sans initialiser les collections
        Utilisateur utilisateurEnSession = utilisateurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        // Crée le projet avec des collections initialisées
        Projet projet = new Projet(projetDTO);
        projet.setStatutProjet(StatutProjet.EN_ATTENTE);
        projet.setDateCreation(new Date());
        projet.setMainAdmin(utilisateurEnSession);
        projet.getMembres().add(utilisateurEnSession);
        projet.getAdmins().add(utilisateurEnSession);

        // Sauvegarde
        Projet projetSauvegarde = projetRepesitory.save(projet);

        // Crée le rôle
        ProjetRole adminRole = new ProjetRole();
        adminRole.setProjet(projetSauvegarde);
        adminRole.setUtilisateur(utilisateurEnSession);
        adminRole.setRoleSecondaire(RoleSecondaire.ADMIN_PROJET);
        projetRoleRepository.save(adminRole);

        return projetSauvegarde;
    }

    /**
     * mettre ajour un projet
     * @param nouvellesDonnees une dto de controller
     * @param utilisateurId a recuperer dans controlleur de puis dto
     * @return
     */

    public Projet mettreAJourProjet( ProjetDTO nouvellesDonnees, Long utilisateurId) {
        Long projetId = nouvellesDonnees.getId();
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);

        verifierPermissionAdminProjet(projet, utilisateur);


        // Mise à jour des champs
        if (nouvellesDonnees.getNomCourt() != null) projet.setNomCourt(nouvellesDonnees.getNomCourt());
        if (nouvellesDonnees.getNomLong() != null) projet.setNomLong(nouvellesDonnees.getNomLong());
        if (nouvellesDonnees.getDescription() != null) projet.setDescription(nouvellesDonnees.getDescription());
        if (nouvellesDonnees.getTheme() != null) projet.setTheme(nouvellesDonnees.getTheme());
        if (nouvellesDonnees.getType() != null) projet.setType(nouvellesDonnees.getType());
        if (nouvellesDonnees.getLicense() != null) projet.setLicense(nouvellesDonnees.getLicense());
        projet.setEstPublic(nouvellesDonnees.isEstPublic());

        return projetRepesitory.save(projet);
    }

    /**
     * suprimet un projet
     * @param projetId a recuperer de puis le dto dans controlleur
     * @param utilisateurId meme truc
     */
    public void supprimerProjet(Long projetId, Long utilisateurId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);

        // Seul l'admin principal ou un admin système peut supprimer un projet
        if (!estAdminPrincipal(projet, utilisateur) && !utilisateur.getRoleSecondaire().equals(RoleType.ADMIN)) {
            throw new IllegalStateException(ERREUR_PERMISSION_REFUSEE);
        }



        // Nettoyer les relations
        projetRoleRepository.deleteByProjetId(projetId);
        projet.getMembres().clear();
        projet.getAdmins().clear();
        projet.getDemandeursEnAttente().clear();

        // Supprimer du groupe si nécessaire
        Groupe groupe = projet.getGroupe();
        if (groupe != null) {
            groupe.getProjets().remove(projet);
            groupeRepository.save(groupe);
        }

        projetRepesitory.delete(projet);
    }

    // ========== GESTION DES STATUTS ==========


    /**
     * methode pour accepter projet
     * @param projetId a recuperer de puis un dto dans controlleur
     * @param adminId a recuperer depuis un dto dans controlleur
     */
    public void accepterProjet(Long projetId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminSysteme(admin);
        verifierStatutProjetPourAcceptation(projet);


        projet.setStatutProjet(StatutProjet.ACCEPTER);
        projet.setDateAcceptation(new Date());
        projet.setDateRejet(null);

        // Ajouter au groupe si nécessaire
        Groupe groupe = projet.getGroupe();
        if (groupe != null && !groupe.getProjets().contains(projet)) {
            groupe.getProjets().add(projet);
            groupeRepository.save(groupe);
        }

        projetRepesitory.save(projet);
    }

    /**
     * methode pour rejeter projett
     * @param projetId recupperer depuis dto
     * @param adminId recupperer depuis dto
     */
    public void rejeterProjet(Long projetId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminSysteme(admin);
        verifierStatutProjetPourRejet(projet);


        projet.setStatutProjet(StatutProjet.REFUSER);
        projet.setDateRejet(new Date());
        projet.setDateAcceptation(null);

        // Retirer du groupe si nécessaire
        Groupe groupe = projet.getGroupe();
        if (groupe != null) {
            groupe.getProjets().remove(projet);
            groupeRepository.save(groupe);
        }

        projetRepesitory.save(projet);
    }


    /**
     * methode pour fermer le controlleur
     * @param projetId a reccuperer depuis dto dans controlleur
     * @param utilisateurId a reccuperer depuis dto dans controlleur
     */
    public void cloturerProjet(Long projetId, Long utilisateurId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);

        verifierPermissionAdminProjet(projet, utilisateur);

        if (!StatutProjet.ACCEPTER.equals(projet.getStatutProjet())) {
            throw new IllegalStateException(ERREUR_SEULS_PROJETS_ACCEPTES_PEUVENT_ETRE_CLOTURES);
        }


        projet.setStatutProjet(StatutProjet.CLOTURE);
        projet.setDateCloture(new Date());
        projetRepesitory.save(projet);
    }

    /**
     * methode pour ouvrir un projet apres la fermeture
     * @param projetId recupperer du dto dans controlleur
     * @param utilisateurId recuperer du dto dans controlleur
     */

    public void reactiverProjet(Long projetId, Long utilisateurId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);

        verifierPermissionAdminProjet(projet, utilisateur);

        if (!StatutProjet.CLOTURE.equals(projet.getStatutProjet())) {
            throw new IllegalStateException(ERREUR_SEULS_PROJETS_CLOTURES_PEUVENT_ETRE_REACTIVES);
        }


        projet.setStatutProjet(StatutProjet.ACCEPTER);
        projet.setDateCloture(null);
        projetRepesitory.save(projet);
    }

    // ========== GESTION DES MEMBRES ==========

    /**
     * methode pour ajouter un mombre dans un projet
     * @param projetId recuperer de puis dto
     * @param utilisateurId recuperer de puis dto (utilisateurdto)
     * @param adminId recuperer de puis dto (utilisateurdto)
     */
    public void ajouterMembre(Long projetId, Long utilisateurId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminProjet(projet, admin);

        if (projet.getMembres().contains(utilisateur)) {
            throw new IllegalStateException(ERREUR_UTILISATEUR_DEJA_MEMBRE);
        }


        projet.getMembres().add(utilisateur);
        utilisateur.getProjets().add(projet);

        // Créer le rôle membre
        ProjetRole membreRole = ProjetRole.builder()
                .projet(projet)
                .utilisateur(utilisateur)
                .roleSecondaire(RoleSecondaire.MEMBRE_PROJET)
                .build();
        projetRoleRepository.save(membreRole);

        projetRepesitory.save(projet);
        utilisateurRepository.save(utilisateur);
    }

    /**
     * methode pour supprimer  un embre
     * @param projetId recuperer de puis dto
     * @param utilisateurId recuperer de puis dto (utilisateurdto)
     * @param adminId recuperer de puis dto (utilisateurdto)
     */
    public void supprimerMembre(Long projetId, Long utilisateurId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminProjet(projet, admin);

        if (!projet.getMembres().contains(utilisateur)) {
            throw new IllegalStateException(ERREUR_UTILISATEUR_NON_MEMBRE);
        }

        if (estAdminPrincipal(projet, utilisateur)) {
            throw new IllegalStateException(ERREUR_ADMIN_PRINCIPAL_PROTECTION);
        }


        projet.getMembres().remove(utilisateur);
        projet.getAdmins().remove(utilisateur);
        projet.getDemandeursEnAttente().remove(utilisateur);
        utilisateur.getProjets().remove(projet);

        // Supprimer les rôles
        projetRoleRepository.deleteByProjetAndUtilisateur(projet, utilisateur);

        projetRepesitory.save(projet);
        utilisateurRepository.save(utilisateur);
    }

    // ========== GESTION DES DEMANDES D'ADHÉSION ==========

    /**
     * accepter  une demande pour rejoindre un projet
     * @param projetId recuperer de puis dto
     * @param demandeurId recuperer de puis dto (utilisateurdto)
     * @param adminId recuperer de puis dto (utilisateurdto)
     */
    public void accepterDemande(Long projetId, Long demandeurId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur demandeur = findUtilisateurById(demandeurId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminProjet(projet, admin);

        if (!projet.getDemandeursEnAttente().remove(demandeur)) {
            throw new IllegalStateException(ERREUR_AUCUNE_DEMANDE);
        }


        projet.getMembres().add(demandeur);
        demandeur.getProjets().add(projet);

        // Créer le rôle membre
        ProjetRole membreRole = ProjetRole.builder()
                .projet(projet)
                .utilisateur(demandeur)
                .roleSecondaire(RoleSecondaire.MEMBRE_PROJET)
                .build();
        projetRoleRepository.save(membreRole);

        projetRepesitory.save(projet);
        utilisateurRepository.save(demandeur);
    }

    /**
     * refuser pdemande pour rejoindre projet
     * @param projetId recuperer de puis dto
     * @param demandeurId recuperer de puis dto (utilisateurdto)
     * @param adminId recuperer de puis dto (utilisateurdto)
     */
    public void refuserDemande(Long projetId, Long demandeurId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur demandeur = findUtilisateurById(demandeurId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminProjet(projet, admin);

        if (!projet.getDemandeursEnAttente().remove(demandeur)) {
            throw new IllegalStateException(ERREUR_AUCUNE_DEMANDE);
        }

        projetRepesitory.save(projet);
    }

    // ========== GESTION DES ADMINISTRATEURS ==========


    /**
     * methode pour ajouter un admine a projet
     * @param projetId recuperer de puis dto
     * @param utilisateurId recuperer de puis dto (utilisateurdto)
     * @param adminId recuperer de puis dto (utilisateurdto)
     */
    public void promouvoirEnAdmin(Long projetId, Long utilisateurId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminPrincipal(projet, admin);

        if (!projet.getMembres().contains(utilisateur)) {
            throw new IllegalStateException("L'utilisateur doit être membre du projet pour devenir admin");
        }


        projet.getAdmins().add(utilisateur);

        // Mettre à jour le rôle
        projetRoleRepository.deleteByProjetAndUtilisateur(projet, utilisateur);
        ProjetRole adminRole = ProjetRole.builder()
                .projet(projet)
                .utilisateur(utilisateur)
                .roleSecondaire(RoleSecondaire.ADMIN_PROJET)
                .build();
        projetRoleRepository.save(adminRole);

        projetRepesitory.save(projet);
    }




    /**
     * * Rétrograde un administrateur en membre
     * @param projetId recuperer de puis dto
     * @param utilisateurId recuperer de puis dto (utilisateurdto)
     * @param adminId recuperer de puis dto (utilisateurdto)
     */
    public void retrograderAdmin(Long projetId, Long utilisateurId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminPrincipal(projet, admin);

        if (estAdminPrincipal(projet, utilisateur)) {
            throw new IllegalStateException(ERREUR_ADMIN_PRINCIPAL_PROTECTION);
        }


        projet.getAdmins().remove(utilisateur);

        // Mettre à jour le rôle
        projetRoleRepository.deleteByProjetAndUtilisateur(projet, utilisateur);
        ProjetRole membreRole = ProjetRole.builder()
                .projet(projet)
                .utilisateur(utilisateur)
                .roleSecondaire(RoleSecondaire.MEMBRE_PROJET)
                .build();
        projetRoleRepository.save(membreRole);

        projetRepesitory.save(projet);
    }



    // ========== MÉTHODES DE CONSULTATION ==========

    public Projet findProjetById(Long id) {
        return projetRepesitory.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ERREUR_PROJET_NON_TROUVE, id)));
    }

    public List<ProjetDTO> getAllProjets() {

       List<Projet> projets =projetRepesitory.findAll();
       return projets.stream()
               .map(ProjetDTO::new)
               .collect(Collectors.toList());
    }

    public List<Projet> getProjetsPublics() {
        return projetRepesitory.findAll().stream()
                .filter(Projet::isEstPublic)
                .collect(Collectors.toList());
    }

    public List<Projet> getProjetsByStatut(StatutProjet statut) {
        return projetRepesitory.findAll().stream()
                .filter(p -> p.getStatutProjet() == statut)
                .collect(Collectors.toList());
    }

    public List<Projet> getProjetsByTheme(String theme) {
        return projetRepesitory.findAll().stream()
                .filter(p -> theme.equals(p.getTheme()))
                .collect(Collectors.toList());
    }

    public List<Projet> rechercherProjets(String motCle) {
        String motCleNormalise = motCle.toLowerCase();
        return projetRepesitory.findAll().stream()
                .filter(p ->
                        (p.getNomCourt() != null && p.getNomCourt().toLowerCase().contains(motCleNormalise)) ||
                                (p.getNomLong() != null && p.getNomLong().toLowerCase().contains(motCleNormalise)) ||
                                (p.getDescription() != null && p.getDescription().toLowerCase().contains(motCleNormalise))
                )
                .collect(Collectors.toList());
    }

    public Set<Utilisateur> getMembres(Long projetId) {
        Projet projet = findProjetById(projetId);
        return new HashSet<>(projet.getMembres());
    }

    public Set<Utilisateur> getAdmins(Long projetId) {
        Projet projet = findProjetById(projetId);
        return new HashSet<>(projet.getAdmins());
    }

    public List<Utilisateur> getDemandesEnAttente(Long projetId, Long adminId) {
        Projet projet = findProjetById(projetId);
        Utilisateur admin = findUtilisateurById(adminId);

        verifierPermissionAdminProjet(projet, admin);

        return new ArrayList<>(projet.getDemandeursEnAttente());
    }

    public Map<String, Object> getStatistiquesProjet(Long projetId) {
        Projet projet = findProjetById(projetId);
        Map<String, Object> stats = new HashMap<>();

        stats.put("nombreMembres", projet.getMembres().size());
        stats.put("nombreAdmins", projet.getAdmins().size());
        stats.put("nombreTaches", projet.getTaches() != null ? projet.getTaches().size() : 0);
        stats.put("nombreReunions", projet.getReunions() != null ? projet.getReunions().size() : 0);
        stats.put("nombreDocuments", projet.getDepotDocuments() != null ? projet.getDepotDocuments().size() : 0);
        stats.put("nombreDemandesEnAttente", projet.getDemandeursEnAttente().size());
        stats.put("dateCreation", projet.getDateCreation());
        stats.put("statut", projet.getStatutProjet());

        return stats;
    }

    // ========== MÉTHODES DE VÉRIFICATION ==========

    public boolean peutVoirProjet(Long projetId, Long utilisateurId) {
        Projet projet = findProjetById(projetId);

        if (projet.isEstPublic()) {
            return true;
        }

        if (utilisateurId != null) {
            try {
                Utilisateur utilisateur = findUtilisateurById(utilisateurId);
                return projet.getMembres().contains(utilisateur);
            } catch (EntityNotFoundException e) {
                return false;
            }
        }

        return false;
    }

    public boolean estMembreProjet(Long projetId, Long utilisateurId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);
        return projet.getMembres().contains(utilisateur);
    }

    public boolean estAdminProjet(Long projetId, Long utilisateurId) {
        Projet projet = findProjetById(projetId);
        Utilisateur utilisateur = findUtilisateurById(utilisateurId);
        return projet.getAdmins().contains(utilisateur);
    }


    // ========== MÉTHODES PRIVÉES D'AIDE ==========

    private Utilisateur findUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(String.format(ERREUR_UTILISATEUR_NON_TROUVE, id)));
    }

    private void verifierPermissionAdminSysteme(Utilisateur utilisateur) {
        if (!RoleType.ADMIN.equals(utilisateur.getRoleSecondaire())) {
            throw new IllegalStateException(ERREUR_PERMISSION_REFUSEE);
        }
    }

    private void verifierPermissionAdminProjet(Projet projet, Utilisateur utilisateur) {
        if (!projet.getAdmins().contains(utilisateur)) {
            throw new IllegalStateException(ERREUR_PERMISSION_REFUSEE);
        }
    }

    private void verifierPermissionAdminPrincipal(Projet projet, Utilisateur utilisateur) {
        if (!estAdminPrincipal(projet, utilisateur)) {
            throw new IllegalStateException(ERREUR_PERMISSION_REFUSEE);
        }
    }

    private boolean estAdminPrincipal(Projet projet, Utilisateur utilisateur) {
        return projet.getMAinAdmin() != null && projet.getMAinAdmin().getId().equals(utilisateur.getId());
    }

    private void verifierStatutProjetPourAcceptation(Projet projet) {
        if (StatutProjet.ACCEPTER.equals(projet.getStatutProjet())) {
            throw new IllegalStateException(ERREUR_PROJET_DEJA_ACCEPTE);
        }
    }

    private void verifierStatutProjetPourRejet(Projet projet) {
        if (StatutProjet.REFUSER.equals(projet.getStatutProjet())) {
            throw new IllegalStateException(ERREUR_PROJET_DEJA_REFUSE);
        }
    }
}