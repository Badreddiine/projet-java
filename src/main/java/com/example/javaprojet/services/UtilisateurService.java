package com.example.javaprojet.services;

import com.example.javaprojet.entity.*;
import com.example.javaprojet.model.RoleType;
import com.example.javaprojet.repo.*;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DefaultAuthenticationEventPublisher;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Scanner;

@Service
public class UtilisateurService {
    Scanner scanner = new Scanner(System.in);
    @Autowired
    UtilisateurRepesitory utilisateurRepesitory;
    @Autowired
    GroupeRepesitory groupeRepesitory;
    @Autowired
    ProjetRepesitory projetRepesitory;
    @Autowired
    AdminRepesitory adminRepesitory;
    private ProjetRoleRepository projetRoleRepository;
    @Autowired
    private DefaultAuthenticationEventPublisher authenticationEventPublisher;

    public void creeCompte(Utilisateur utilisateur) {
        List<Utilisateur> listExist = utilisateurRepesitory.findByEmail(utilisateur.getEmail());
        if(listExist.isEmpty()){
            utilisateur.setRole(RoleType.GUEST);
            //utilisateur.setGroupes(RoleType.GUEST);
            utilisateurRepesitory.save(utilisateur);
            System.out.println("Compte créé avec succès !");
        }
        else{
            System.out.println("User deja exist!");
        }
    }
    public boolean seConnecter(String email, String motPasse) {
        List<Utilisateur> userExist = utilisateurRepesitory.findByEmailAndMotDePasse(email, motPasse);
        if(!userExist.isEmpty()){
            return true;
        }
        else{
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

        // 2. Vérification de l'existence des entités associées
        Groupe groupe = groupeRepesitory.findById(projet.getGroupe().getId())
                .orElseThrow(() -> new EntityNotFoundException("Groupe introuvable"));

       Utilisateur admin = utilisateurRepesitory.findById(projet.getAdmin().getId())
         .orElseThrow(() -> new EntityNotFoundException("Admin introuvable"));

        if (!groupe.getMembres().contains(admin)) {
            throw new IllegalStateException("L'admin doit être membre du groupe");
        }

        projet.setEtat("EN_ATTENTE");
        projet.setGroupe(groupe);
        projet.setAdmin(admin);
        projet.setDateCreation(new Date());
        projet.getMembres().add(admin);
        projetRepesitory.save(projet);
    }

    @Transactional
    public void modifierProfil(Long idUser, Utilisateur utilisateurModifie) {
        Optional<Utilisateur> userExist = utilisateurRepesitory.findById(idUser);
        if (userExist.isPresent()) {
            Utilisateur user = userExist.get();

            if (!user.getEmail().equals(utilisateurModifie.getEmail()) && !utilisateurRepesitory.findByEmail(utilisateurModifie.getEmail()).isEmpty()) {
                throw new RuntimeException("Cet email est deja utilise par un autre utilisateur.");
            }
            user.setNom(utilisateurModifie.getNom());
            user.setPrenom(utilisateurModifie.getPrenom());
            user.setEmail(utilisateurModifie.getEmail());

            if (utilisateurModifie.getMotDePasse() != null && !utilisateurModifie.getMotDePasse().isEmpty()) {
                user.setMotDePasse(utilisateurModifie.getMotDePasse());
            }

            utilisateurRepesitory.save(user);
        } else {
            throw new RuntimeException("Utilisateur non trouve avec lID: " + idUser);
        }

    }

    public List<Projet> listeProjets(){
        return projetRepesitory.findByEstPublic(true);
    }

    public void adminProjet(Long idProjet) {
        Optional<Projet> projet = projetRepesitory.findById(idProjet);
        if (projet.isPresent()) {
            System.out.println("l'Admin de ce Projet de " + projet.get().getNomLong() + "est" +projet.get().getAdmin());
        }
    }

    public List<Utilisateur> getMembresDuProjet(Long projetId, Long utilisateurId) {
        List<Utilisateur> membres = projetRepesitory.findMembresIfUserHasAccess(projetId, utilisateurId);
        if (membres.isEmpty()) {
            throw new SecurityException("Accès refusé ou projet inexistant");
        }
        return membres;
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

}
