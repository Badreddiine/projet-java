package com.example.javaprojet.services;

import com.example.javaprojet.dto.ProjetDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.*;
import com.example.javaprojet.enums.RoleSecondaire;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.enums.StatutProjet;
import com.example.javaprojet.repo.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;
import java.util.stream.Collectors;

@Service
public class UtilisateurService {

    @Autowired
    UtilisateurRepesitory utilisateurRepesitory;

    @Autowired
    GroupeRepesitory groupeRepesitory;

    @Autowired
    ProjetRepesitory projetRepesitory;
    @Autowired
    private ProjetRoleRepository projetRoleRepository;

    @Autowired
    private DefaultAuthenticationEventPublisher authenticationEventPublisher;

    @Autowired
    PasswordEncoder passwordEncoder;

    public void creeCompte(UtilisateurDTO utilisateurDTO) throws Exception {
        List<Utilisateur> listExist = utilisateurRepesitory.findByEmail(utilisateurDTO.getEmail());
        if (listExist.isEmpty()) {
            Utilisateur utilisateur = convertToEntity(utilisateurDTO);
            utilisateur.setRole(RoleType.GUEST);
            // TODO : This should change later
            utilisateur.setRoleSecondaire(RoleSecondaire.GUESS);
            if (utilisateurDTO.hasPassword()) {
                utilisateur.setMotDePasse(passwordEncoder.encode(utilisateurDTO.getMotDePasse()));
            }

            utilisateurRepesitory.save(utilisateur);
            System.out.println("Compte créé avec succès !");
        } else {
            throw new Exception("User already exist!");
        }
    }

    public boolean seConnecter(String email, String motPasse) {
        List<Utilisateur> userExist = utilisateurRepesitory.findByEmailAndMotDePasse(email, motPasse);
        if (!userExist.isEmpty()) {
            Utilisateur utilisateur = userExist.get(0);
            setConnecte(utilisateur, true);
            return true;
        } else {
            return false;
        }
    }

    @Transactional
    public void demanderCreationProjet(Projet projet) {
        if (projet.getGroupe() == null || projet.getAdmin() == null) {
            throw new IllegalArgumentException("Le groupe et l'admin du projet sont obligatoires");
        }

        if (projet.getNomCourt() == null || projet.getNomCourt().isBlank()) {
            throw new IllegalArgumentException("Le nom court du projet est obligatoire");
        }

        Groupe groupe = groupeRepesitory.findById(projet.getGroupe().getId())
                .orElseThrow(() -> new EntityNotFoundException("Groupe introuvable"));

        Utilisateur admin = utilisateurRepesitory.findById(projet.getAdmin().getId())
                .orElseThrow(() -> new EntityNotFoundException("Admin introuvable"));

        if (!groupe.getMembres().contains(admin)) {
            throw new IllegalStateException("L'admin doit être membre du groupe");
        }

        projet.setStatutProjet(StatutProjet.EN_ATTENTE);
        projet.setGroupe(groupe);
        projet.setAdmin(admin);
        projet.setDateCreation(new Date());
        projet.getMembres().add(admin);
        projetRepesitory.save(projet);
    }

    @Transactional
    public void modifierProfil(Long idUser, UtilisateurDTO utilisateurModifieDTO) {
        Optional<Utilisateur> userExist = utilisateurRepesitory.findById(idUser);
        if (userExist.isPresent()) {
            Utilisateur user = userExist.get();

            if (!user.getEmail().equals(utilisateurModifieDTO.getEmail()) &&
                    !utilisateurRepesitory.findByEmail(utilisateurModifieDTO.getEmail()).isEmpty()) {
                throw new RuntimeException("Cet email est deja utilise par un autre utilisateur.");
            }

            user.setNom(utilisateurModifieDTO.getNom());
            user.setPrenom(utilisateurModifieDTO.getPrenom());
            user.setEmail(utilisateurModifieDTO.getEmail());

            // Handle password update if provided
            if (utilisateurModifieDTO.hasPassword()) {
                user.setMotDePasse(passwordEncoder.encode(utilisateurModifieDTO.getMotDePasse()));
            }

            // Update other fields if needed
            if (utilisateurModifieDTO.getAvatar() != null) {
                user.setAvatar(utilisateurModifieDTO.getAvatar());
            }

            utilisateurRepesitory.save(user);
        } else {
            throw new RuntimeException("Utilisateur non trouve avec lID: " + idUser);
        }
    }

    public List<Projet> listeProjets() {
        return projetRepesitory.findByEstPublic(true);
    }

    public void adminProjet(Long idProjet) {
        Optional<Projet> projet = projetRepesitory.findById(idProjet);
        if (projet.isPresent()) {
            System.out.println("l'Admin de ce Projet de " + projet.get().getNomLong() +
                    " est " + projet.get().getAdmin());
        }
    }

    public List<UtilisateurDTO> getMembresDuProjet(Long projetId, Long utilisateurId) {
        List<Utilisateur> membres = projetRepesitory.findMembresIfUserHasAccess(projetId, utilisateurId);
        if (membres.isEmpty()) {
            throw new SecurityException("Accès refusé ou projet inexistant");
        }
        return membres.stream()
                .map(UtilisateurDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public void demanderRejoindreProjet(Long projetId, Long utilisateurId) {
        Projet projet = projetRoleRepository.findById(projetId)
                .orElseThrow(() -> new EntityNotFoundException("Projet non trouvé")).getProjet();

        Utilisateur utilisateur = utilisateurRepesitory.findById(utilisateurId)
                .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

        if (projet.getMembres().contains(utilisateur)) {
            throw new IllegalStateException("Déjà membre du projet");
        }

        if (projet.getDemandeursEnAttente().contains(utilisateur)) {
            throw new IllegalStateException("Demande déjà en attente");
        }

        projet.getDemandeursEnAttente().add(utilisateur);
        projetRepesitory.save(projet);
    }

    public void afficherMesInformations(Long userId) {
        Utilisateur utilisateur = utilisateurRepesitory.findById(userId).orElse(null);
        if (utilisateur != null) {
            Hibernate.initialize(utilisateur.getProjets());
            System.out.println(utilisateur);
        } else {
            System.out.println("Utilisateur non trouvé.");
        }
    }

    public UtilisateurDTO getUtilisateurById(Long id) {
        Optional<Utilisateur> utilisateur = utilisateurRepesitory.findById(id);
        return utilisateur.map(UtilisateurDTO::new).orElse(null);
    }

    @Transactional
    public void setConnecte(Utilisateur utilisateur, boolean estConnecte) {
        if (utilisateur != null) {
            utilisateur.setEstConnecte(estConnecte);
            if (estConnecte) {
                setDerniereConnexion(utilisateur, new Date());
            }
            utilisateurRepesitory.save(utilisateur);
        }
    }

    @Transactional
    public void setDerniereConnexion(Utilisateur utilisateur, Date date) {
        if (utilisateur != null) {
            utilisateur.setDerniereConnexion(date);
            utilisateurRepesitory.save(utilisateur);
        }
    }

    @Transactional
    public UtilisateurDTO save(UtilisateurDTO utilisateurDTO) {
        Utilisateur utilisateur = convertToEntity(utilisateurDTO);
        Utilisateur saved = utilisateurRepesitory.save(utilisateur);
        return new UtilisateurDTO(saved);
    }

    public List<UtilisateurDTO> findByNom(String nom) {
        List<Utilisateur> utilisateurs = utilisateurRepesitory.findByNomContainingIgnoreCase(nom);
        return utilisateurs.stream()
                .map(UtilisateurDTO::new)
                .collect(Collectors.toList());
    }

    @Transactional
    public boolean seDeconnecter(Long utilisateurId) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepesitory.findById(utilisateurId);
        if (utilisateurOpt.isPresent()) {
            setConnecte(utilisateurOpt.get(), false);
            return true;
        }
        return false;
    }

    @Transactional
    public boolean supprimerCompte(Long utilisateurId) {
        Optional<Utilisateur> utilisateur = utilisateurRepesitory.findById(utilisateurId);
        if (utilisateur.isPresent()) {
            utilisateurRepesitory.delete(utilisateur.get());
            return true;
        }
        return false;
    }

    public List<Projet> getProjetsByUtilisateur(Long utilisateurId) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepesitory.findById(utilisateurId);
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            Hibernate.initialize(utilisateur.getProjets());
            return (List<Projet>) utilisateur.getProjets();
        }
        return List.of();
    }

    @Transactional
    public boolean changerRole(Long utilisateurId, RoleType nouveauRole) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepesitory.findById(utilisateurId);
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            utilisateur.setRole(nouveauRole);
            utilisateurRepesitory.save(utilisateur);
            return true;
        }
        return false;
    }

    public Optional<UtilisateurDTO> findById(Long id) {
        Optional<Utilisateur> utilisateur = utilisateurRepesitory.findById(id);
        return utilisateur.map(UtilisateurDTO::new);
    }

    public UtilisateurDTO getUtilisateurByEmail(String email) {
        Optional<Utilisateur> utilisateur = utilisateurRepesitory.getDistinctByEmail(email);
        return utilisateur.map(UtilisateurDTO::new).orElse(null);
    }

    public boolean hasCalendar(Long utilisateurId, Long calendarId) {
        return utilisateurRepesitory.hasCalendar(utilisateurId, calendarId);
    }

    // Helper method to convert DTO to Entity
    private Utilisateur convertToEntity(UtilisateurDTO dto) {
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setId(dto.getId());
        utilisateur.setNom(dto.getNom());
        utilisateur.setPrenom(dto.getPrenom());
        utilisateur.setEmail(dto.getEmail());
        utilisateur.setAvatar(dto.getAvatar());
        utilisateur.setEstEnLigne(dto.isEstEnLigne());
        return utilisateur;
    }

    // Helper method to get entity by ID (for internal use)
    private Utilisateur getUtilisateurEntityById(Long id) {
        return utilisateurRepesitory.findById(id).orElse(null);
    }
}