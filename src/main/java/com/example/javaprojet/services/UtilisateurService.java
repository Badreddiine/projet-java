package com.example.javaprojet.services;

import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.ProjetRepesitory;
import com.example.javaprojet.repo.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@Transactional
public class UtilisateurService {

    private final ProjetRepesitory projetRepesitory;
    private PasswordEncoder passwordEncoder;
    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurService(UtilisateurRepository utilisateurRepository,
                              PasswordEncoder passwordEncoder,
                              ProjetRepesitory projetRepesitory) {
        this.utilisateurRepository = utilisateurRepository;
        this.passwordEncoder = passwordEncoder;
        this.projetRepesitory = projetRepesitory;
    }

    /**
     * creation utilisateur vas etre utiliser dans incription et
     * verifications dans controlleur
     * @param utilisateurDTO data de utilisateur qu'on vas creer
     * @return entite de utilisateur cree
     */
    public Utilisateur creerUtilisateur(UtilisateurDTO utilisateurDTO) {
        if (utilisateurDTO.getMotDePasse() != null && !utilisateurDTO.getMotDePasse().isEmpty()) {
            String motDePasseEncode = passwordEncoder.encode(utilisateurDTO.getMotDePasse());
            utilisateurDTO.setMotDePasse(motDePasseEncode);
        }

        if (utilisateurDTO.getDateInscription() == null) {
            utilisateurDTO.setDateInscription(new Date());
        }
        Utilisateur utilisateur= new Utilisateur(utilisateurDTO);

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * changes mot de passe de utilistaeur
     * elle ne verifis pas utilisateur courant (ou admin) il faut les verifier avant de etuliser
     * @param utilisateurId
     * @param nouveauMotDePasse
     * @return
     */
    public void changerMotDePasse(Long utilisateurId, String nouveauMotDePasse) throws RuntimeException {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));

    }

    // Nouvelles méthodes pour la gestion des projets

    /**
     * Ajouter un utilisateur comme membre d'un projet
     * il faut verifier dans le controlleur si le rolle de user est admin_projet
     * verifier si il est en attend deja
     */
    public void accepterMembreAuProjet(Utilisateur utilisateur, Projet projet) {
        projet.getMembres().add(utilisateur);
        projetRepesitory.save(projet);
    }

    /**
     * Retirer un utilisateur d'un projet
     * verifies autorisattion
     */
    public void retirerMembreDuProjet(Utilisateur utilisateur, Projet projet) {
        projet.getMembres().remove(utilisateur);
        projet.getAdmins().remove(utilisateur);
        projet.getDemandeursEnAttente().remove(utilisateur);
        projetRepesitory.save(projet);
    }

    /**
     * Ajouter un utilisateur comme admin d'un projet
     * verifier autorisation
     */
    public void ajouterAdminAuProjet(Utilisateur utilisateur, Projet projet) {
        // Ajouter comme membre et admin
        projet.getMembres().add(utilisateur);
        projet.getAdmins().add(utilisateur);
        projetRepesitory.save(projet);
    }

    /**
     * Retirer les droits d'admin d'un utilisateur (mais reste membre)
     * verifier autorisation
     */
    public void retirerAdminDuProjet(Utilisateur utilisateur, Projet projet) {
        // Vérifier que ce n'est pas l'admin principal
        if (projet.getAdmin() != null && projet.getAdmin().getId().equals(utilisateur.getId())) {
            throw new RuntimeException("Impossible de retirer les droits de l'admin principal");
        }

        projet.getAdmins().remove(utilisateur);
        projetRepesitory.save(projet);
    }

    /**
     * Ajouter une demande d'adhésion en attente
     */
    public void ajouterDemandeAdhesion(Utilisateur utilisateur, Projet projet) {
        // Vérifier que l'utilisateur n'est pas déjà membre
        if (projet.getMembres().contains(utilisateur)) {
            throw new RuntimeException("L'utilisateur est déjà membre du projet");
        }

        projet.getDemandeursEnAttente().add(utilisateur);
        projetRepesitory.save(projet);
    }

    /**
     * Accepter une demande d'adhésion
     */
    public void accepterDemandeAdhesion(Utilisateur utilisateur, Projet projet) {
        if (projet.getDemandeursEnAttente().contains(utilisateur)) {
            projet.getDemandeursEnAttente().remove(utilisateur);
            projet.getMembres().add(utilisateur);
            projetRepesitory.save(projet);
        } else {
            throw new RuntimeException("Aucune demande en attente pour cet utilisateur");
        }
    }

    /**
     * Rejeter une demande d'adhésion
     */
    public void rejeterDemandeAdhesion(Utilisateur utilisateur, Projet projet) {
        projet.getDemandeursEnAttente().remove(utilisateur);
        projetRepesitory.save(projet);
    }

    /**
     * Vérifier si un utilisateur est membre d'un projet
     * contains a verfier
     */
    public boolean estMembreDuProjet(Utilisateur utilisateur, Projet projet) {
        return projet.getMembres().contains(utilisateur);
    }

    /**
     * Vérifier si un utilisateur est admin d'un projet
     */
    public boolean estAdminDuProjet(Utilisateur utilisateur, Projet projet) {
        return projet.getAdmins().contains(utilisateur);
    }

    /**
     * Vérifier si un utilisateur est l'admin principal d'un projet
     */
    public boolean estAdminPrincipalDuProjet(Utilisateur utilisateur, Projet projet) {
        return projet.getAdmin() != null && projet.getAdmin().getId().equals(utilisateur.getId());
    }

    /**
     * Obtenir un utilisateur par ID
     */
    public Optional<Utilisateur> getUtilisateurById(Long id) {
        return utilisateurRepository.findById(id);
    }

    /**
     * Obtenir un utilisateur par ID - version qui lance une exception si non trouvé
     * Pour compatibilité avec le controller
     */
    public Utilisateur findUtilisateurById(Long id) {
        return utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir un utilisateur par email
     */
    public Optional<Utilisateur> getUtilisateurByEmail(String email) {
        return utilisateurRepository.findByEmail(email).stream().findFirst();
    }

    /**
     * Obtenir tous les utilisateurs
     * verifier si admin
     */
    public List<Utilisateur> getAllUtilisateurs() {
        return utilisateurRepository.findAll();
    }

    /**
     * Supprimer un utilisateur
     */
    public void supprimerUtilisateur(Utilisateur utilisateur) {
        utilisateurRepository.delete(utilisateur);
    }

    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }
}