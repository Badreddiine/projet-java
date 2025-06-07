package com.example.javaprojet.services;

import com.example.javaprojet.config.SecurityConfig;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.dto.ProjetDTO;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.repo.UtilisateurRepository;
import com.example.javaprojet.repo.ProjetRepesitory;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class UtilisateurService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private ProjetRepesitory projetRepository;

    // CORRECTION 1: Utiliser l'injection de dépendance au lieu de créer une nouvelle instance
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ========== GESTION DES UTILISATEURS ==========

    /**
     * Créer un nouvel utilisateur
     */
    public UtilisateurDTO creerUtilisateur(UtilisateurDTO utilisateurDTO) {
        // Vérifier si l'email existe déjà
        if (!utilisateurRepository.findByEmail(utilisateurDTO.getEmail()).isEmpty()) {
            throw new RuntimeException("Un utilisateur avec cet email existe déjà");
        }

        // CORRECTION 2: Crypter le mot de passe AVANT de créer l'entité
        if (utilisateurDTO.getMotDePasse() != null && !utilisateurDTO.getMotDePasse().isEmpty()) {
            String motDePasseCrypte = passwordEncoder.encode(utilisateurDTO.getMotDePasse());
            utilisateurDTO.setMotDePasse(motDePasseCrypte);
            System.out.println("========================================================");
            System.out.println("Mot de passe original reçu (masqué pour sécurité)");
            System.out.println("Mot de passe crypté: " + motDePasseCrypte);
            System.out.println("========================================================");
        }

        // Définir la date d'inscription
        if (utilisateurDTO.getDateInscription() == null) {
            utilisateurDTO.setDateInscription(new Date());
        }

        // Définir le rôle par défaut
        if (utilisateurDTO.getRole() == null) {
            utilisateurDTO.setRole(RoleType.GUEST);
        }

        // CORRECTION 3: Maintenant le DTO contient déjà le mot de passe crypté
        Utilisateur utilisateur = new Utilisateur(utilisateurDTO);
        Utilisateur utilisateurSauvegarde = utilisateurRepository.save(utilisateur);

        // CORRECTION 4: Nettoyer le mot de passe du DTO de retour pour sécurité
        UtilisateurDTO resultat = new UtilisateurDTO(utilisateurSauvegarde);
        resultat.setMotDePasse(null); // Ne pas retourner le mot de passe crypté

        return resultat;
    }

    /**
     * Modifier le profil d'un utilisateur
     */
    public UtilisateurDTO modifierProfil(Long utilisateurId, UtilisateurDTO utilisateurDTO) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        // Vérifier si le nouvel email est déjà utilisé par un autre utilisateur
        if (!utilisateur.getEmail().equals(utilisateurDTO.getEmail())) {
            List<Utilisateur> utilisateursAvecEmail = utilisateurRepository.findByEmail(utilisateurDTO.getEmail());
            if (!utilisateursAvecEmail.isEmpty()) {
                throw new RuntimeException("Cet email est déjà utilisé par un autre utilisateur");
            }
        }

        // Mettre à jour les champs
        utilisateur.setNom(utilisateurDTO.getNom());
        utilisateur.setPrenom(utilisateurDTO.getPrenom());
        utilisateur.setEmail(utilisateurDTO.getEmail());

        // Encoder le nouveau mot de passe si fourni
        if (utilisateurDTO.getMotDePasse() != null && !utilisateurDTO.getMotDePasse().isEmpty()) {
            utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDTO.getMotDePasse()));
        }

        Utilisateur utilisateurModifie = utilisateurRepository.save(utilisateur);
        UtilisateurDTO resultat = new UtilisateurDTO(utilisateurModifie);
        resultat.setMotDePasse(null); // Sécurité
        return resultat;
    }

    /**
     * Changer le mot de passe d'un utilisateur
     */
    public void changerMotDePasse(Long utilisateurId, String nouveauMotDePasse) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        utilisateur.setMotDePasse(passwordEncoder.encode(nouveauMotDePasse));
        utilisateurRepository.save(utilisateur);
    }

    /**
     * Obtenir un utilisateur par ID
     */
    public Utilisateur getUtilisateurById(Long id) {
        Utilisateur utilisateur = utilisateurRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        return utilisateur;
    }

    public Utilisateur saveUtilisateur(Utilisateur utilisateur) {
        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Obtenir un utilisateur par email
     */
    public UtilisateurDTO getUtilisateurByEmail(String email) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByEmail(email);
        if (utilisateurs.isEmpty()) {
            throw new EntityNotFoundException("Utilisateur non trouvé avec l'email: " + email);
        }
        UtilisateurDTO resultat = new UtilisateurDTO(utilisateurs.get(0));
        resultat.setMotDePasse(null); // Sécurité
        return resultat;
    }

    /**
     * Obtenir tous les utilisateurs
     */
    public List<UtilisateurDTO> getAllUtilisateurs() {
        List<Utilisateur> utilisateurs = utilisateurRepository.findAll();
        return utilisateurs.stream()
                .map(u -> {
                    UtilisateurDTO dto = new UtilisateurDTO(u);
                    dto.setMotDePasse(null); // Sécurité
                    return dto;
                })
                .collect(Collectors.toList());
    }

    /**
     * Rechercher des utilisateurs par nom
     */
    public List<UtilisateurDTO> rechercherUtilisateursParNom(String nom) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByNomContainingIgnoreCase(nom);
        return utilisateurs.stream()
                .map(u -> {
                    UtilisateurDTO dto = new UtilisateurDTO(u);
                    dto.setMotDePasse(null); // Sécurité
                    return dto;
                })
                .collect(Collectors.toList());
    }

    public void supprimerCompte(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));
        utilisateurRepository.delete(utilisateur);
    }

    /**
     * Changer le rôle d'un utilisateur
     */
    public UtilisateurDTO changerRole(UtilisateurDTO utilisateurDTO, RoleType nouveauRole) {
        Long utilisateurId = utilisateurDTO.getId();
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        utilisateur.setRole(nouveauRole);
        Utilisateur utilisateurModifie = utilisateurRepository.save(utilisateur);
        UtilisateurDTO resultat = new UtilisateurDTO(utilisateurModifie);
        resultat.setMotDePasse(null); // Sécurité
        return resultat;
    }

    // ========== GESTION DE CONNEXION ==========

    /**
     * Connecter un utilisateur
     */
    public UtilisateurDTO seConnecter(String email, String motDePasse) {
        List<Utilisateur> utilisateurs = utilisateurRepository.findByEmail(email);
        if (utilisateurs.isEmpty()) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        Utilisateur utilisateur = utilisateurs.get(0);
        if (!passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
            throw new RuntimeException("Email ou mot de passe incorrect");
        }

        utilisateur.setEstConnecte(true);
        utilisateur.setDerniereConnexion(new Date());
        Utilisateur utilisateurConnecte = utilisateurRepository.save(utilisateur);

        UtilisateurDTO resultat = new UtilisateurDTO(utilisateurConnecte);
        resultat.setMotDePasse(null); // Sécurité
        return resultat;
    }

    /**
     * Déconnecter un utilisateur
     */
    public void seDeconnecter(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        utilisateur.setEstConnecte(false);
        utilisateurRepository.save(utilisateur);
    }

    // ========== GESTION DES PROJETS ==========

    /**
     * Obtenir les projets d'un utilisateur
     */
    public List<ProjetDTO> getProjetsByUtilisateur(Long utilisateurId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        List<Projet> projets = (List<Projet>) utilisateur.getProjets();
        return projets.stream()
                .map(ProjetDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Demander à rejoindre un projet
     */
    public void demanderRejoindreProjet(Long utilisateurId, Long projetId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

        if (projet.getMembres().contains(utilisateur)) {
            throw new RuntimeException("Vous êtes déjà membre de ce projet");
        }

        if (projet.getDemandeursEnAttente().contains(utilisateur)) {
            throw new RuntimeException("Votre demande est déjà en attente");
        }

        projet.getDemandeursEnAttente().add(utilisateur);
        projetRepository.save(projet);
    }

    /**
     * Accepter une demande d'adhésion
     */
    public void accepterDemandeAdhesion(Long utilisateurId, Long projetId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

        if (!projet.getDemandeursEnAttente().contains(utilisateur)) {
            throw new RuntimeException("Aucune demande en attente pour cet utilisateur");
        }

        projet.getDemandeursEnAttente().remove(utilisateur);
        projet.getMembres().add(utilisateur);
        projetRepository.save(projet);
    }

    /**
     * Rejeter une demande d'adhésion
     */
    public void rejeterDemandeAdhesion(Long utilisateurId, Long projetId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

        projet.getDemandeursEnAttente().remove(utilisateur);
        projetRepository.save(projet);
    }

    // ========== MÉTHODES DE VÉRIFICATION ==========

    /**
     * Vérifier si un utilisateur est membre d'un projet
     */
    public boolean estMembreDuProjet(Long utilisateurId, Long projetId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElse(null);
        Projet projet = projetRepository.findById(projetId).orElse(null);

        if (utilisateur == null || projet == null) {
            return false;
        }

        return projet.getMembres().contains(utilisateur);
    }

    /**
     * Vérifier si un utilisateur est admin d'un projet
     */
    public boolean estAdminDuProjet(Long utilisateurId, Long projetId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElse(null);
        Projet projet = projetRepository.findById(projetId).orElse(null);

        if (utilisateur == null || projet == null) {
            return false;
        }

        return projet.getAdmins().contains(utilisateur);
    }

    /**
     * Vérifier si un utilisateur est l'admin principal d'un projet
     */
    public boolean estAdminPrincipalDuProjet(Long utilisateurId, Long projetId) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElse(null);
        Projet projet = projetRepository.findById(projetId).orElse(null);

        if (utilisateur == null || projet == null || projet.getAdmin() == null) {
            return false;
        }

        return projet.getAdmin().getId().equals(utilisateur.getId());
    }

    /**
     * Obtenir les projets publics
     */
    public List<ProjetDTO> getProjetsPuliques() {
        List<Projet> projets = projetRepository.findByEstPublic(true);
        return projets.stream()
                .map(ProjetDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Obtenir les membres d'un projet (si l'utilisateur a accès)
     */
    public List<UtilisateurDTO> getMembresDuProjet(Long projetId, Long utilisateurId) {
        // Vérifier si l'utilisateur a accès au projet
        if (!estMembreDuProjet(utilisateurId, projetId)) {
            throw new SecurityException("Accès refusé - vous n'êtes pas membre de ce projet");
        }

        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé"));

        return projet.getMembres().stream()
                .map(u -> {
                    UtilisateurDTO dto = new UtilisateurDTO(u);
                    dto.setMotDePasse(null); // Sécurité
                    return dto;
                })
                .collect(Collectors.toList());
    }
}