package com.example.javaprojet.services;

import com.example.javaprojet.dto.SalleDiscussionDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.TypeSalle;
import com.example.javaprojet.repo.GroupeRepesitory;
import com.example.javaprojet.repo.ProjetRepesitory;
import com.example.javaprojet.repo.SalleDiscussionRepository;
import com.example.javaprojet.repo.UtilisateurRepesitory;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SalleDiscussionService {

    private final SalleDiscussionRepository salleDiscussionRepository;
    private final UtilisateurRepesitory utilisateurRepository;
    private final ProjetRepesitory projetRepository;
    private final GroupeRepesitory groupeRepository;

    // Fixed: Return SalleDiscussion entity instead of DTO
    public Optional<SalleDiscussionDTO> getSalleById(Long id) {
        return salleDiscussionRepository.findById(id)
                .map(SalleDiscussionDTO::new);
    }

    @Transactional
    public SalleDiscussion creerSalle(SalleDiscussionDTO salleDTO, Long createurId) {
        Utilisateur createur = utilisateurRepository.findById(createurId)
                .orElseThrow(() -> new RuntimeException("Créateur non trouvé"));

        SalleDiscussion.SalleDiscussionBuilder builder = SalleDiscussion.builder()
                .nom(salleDTO.getNom())
                .description(salleDTO.getDescription())
                .typeSalle(salleDTO.getTypeSalle())
                .estPublique(salleDTO.isEstPublique())
                .dateCreation(new Date())
                .createur(createur);

        // Handle different types of rooms
        switch (salleDTO.getTypeSalle()) {
            case GROUPE:
                if (salleDTO.getIdGroupe() != null) {
                    Groupe groupe = groupeRepository.findById(salleDTO.getIdGroupe())
                            .orElseThrow(() -> new RuntimeException("Groupe non trouvé"));
                    builder.groupe(groupe).membres(new HashSet<>(groupe.getMembres()));
                }
                break;
            case PROJET:
                if (salleDTO.getIdProjet() != null) {
                    Projet projet = projetRepository.findById(salleDTO.getIdProjet())
                            .orElseThrow(() -> new RuntimeException("Projet non trouvé"));
                    builder.projet(projet).membres(new HashSet<>(projet.getMembres()));
                }
                break;
            case PRIVEE:
                Set<Utilisateur> membresPrives = new HashSet<>();
                membresPrives.add(createur);
                // Add logic to add the other user if specified
                builder.membres(membresPrives).estPublique(false);
                break;
            case GENERALE:
                builder.membres(new HashSet<>());
                break;
        }

        return salleDiscussionRepository.save(builder.build());
    }

    @Transactional
    public SalleDiscussion creerSalleGroupe(String nom, String description, Groupe groupe, Utilisateur createur) {
        SalleDiscussion salle = SalleDiscussion.builder()
                .nom(nom)
                .description(description)
                .typeSalle(TypeSalle.GROUPE)
                .estPublique(true)
                .dateCreation(new Date())
                .groupe(groupe)
                .createur(createur)
                .membres(new HashSet<>(groupe.getMembres()))
                .build();
        return salleDiscussionRepository.save(salle);
    }

    @Transactional
    public SalleDiscussion creerSalleProjet(String nom, String description, Projet projet, Utilisateur createur) {
        SalleDiscussion salle = SalleDiscussion.builder()
                .nom(nom)
                .description(description)
                .typeSalle(TypeSalle.PROJET)
                .estPublique(true)
                .dateCreation(new Date())
                .projet(projet)
                .createur(createur)
                .membres(new HashSet<>(projet.getMembres()))
                .build();
        return salleDiscussionRepository.save(salle);
    }

    @Transactional
    public SalleDiscussion creerSalleGenerale(String nom, String description, Utilisateur createur) {
        SalleDiscussion salle = SalleDiscussion.builder()
                .nom(nom)
                .description(description)
                .typeSalle(TypeSalle.GENERALE)
                .estPublique(true)
                .dateCreation(new Date())
                .createur(createur)
                .membres(new HashSet<>())
                .build();
        return salleDiscussionRepository.save(salle);
    }

    @Transactional
    public SalleDiscussion creerSessionPrivee(Utilisateur utilisateur1, Utilisateur utilisateur2) {
        // Vérifier si une session privée existe déjà entre ces utilisateurs
        List<SalleDiscussion> sessionsPriveesExistantes =
                salleDiscussionRepository.findByTypeAndMembres(TypeSalle.PRIVEE, utilisateur1, utilisateur2);

        if (!sessionsPriveesExistantes.isEmpty()) {
            return sessionsPriveesExistantes.get(0);
        }

        // Créer un nom unique pour la session privée
        String nom = "Chat privé: " + utilisateur1.getNom() + " & " + utilisateur2.getNom();

        Set<Utilisateur> membres = new HashSet<>();
        membres.add(utilisateur1);
        membres.add(utilisateur2);

        SalleDiscussion salle = SalleDiscussion.builder()
                .nom(nom)
                .description("Session de conversation privée")
                .typeSalle(TypeSalle.PRIVEE)
                .estPublique(false)
                .dateCreation(new Date())
                .createur(utilisateur1)
                .membres(membres)
                .build();

        return salleDiscussionRepository.save(salle);
    }

    // Fixed: Return List<SalleDiscussion> instead of List<SalleDiscussionDTO>
    public List<SalleDiscussion> getSallesUtilisateur(UtilisateurDTO utilisateurDTO) {
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurDTO.getId())
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        return salleDiscussionRepository.findByMembresContaining(utilisateur);
    }

    public List<SalleDiscussion> getSessionsPriveesUtilisateur(Utilisateur utilisateur) {
        return salleDiscussionRepository.findByTypeSalleAndMembresContaining(TypeSalle.PRIVEE, utilisateur);
    }

    public List<SalleDiscussion> getSallesProjet(Long projetId) {
        return salleDiscussionRepository.findByProjet_Id(projetId);
    }

    public List<SalleDiscussion> getSallesGroupe(Long groupeId) {
        return salleDiscussionRepository.findByGroupe_Id(groupeId);
    }

    @Transactional
    public void ajouterMembre(Long salleId, Long utilisateurId) {
        SalleDiscussion salle = salleDiscussionRepository.findById(salleId)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        salle.getMembres().add(utilisateur);
        salleDiscussionRepository.save(salle);
    }

    @Transactional
    public void retirerMembre(Long salleId, Long utilisateurId) {
        SalleDiscussion salle = salleDiscussionRepository.findById(salleId)
                .orElseThrow(() -> new RuntimeException("Salle non trouvée"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));

        salle.getMembres().remove(utilisateur);
        salleDiscussionRepository.save(salle);
    }

    @Transactional
    public void ajouterUtilisateur(SalleDiscussion salle, Utilisateur utilisateur) {
        salle.getMembres().add(utilisateur);
        salleDiscussionRepository.save(salle);
    }

    @Transactional
    public void supprimerUtilisateur(SalleDiscussion salle, Utilisateur utilisateur) {
        salle.getMembres().remove(utilisateur);
        salleDiscussionRepository.save(salle);
    }

    @Transactional
    public void supprimerSalle(Long salleId) {
        salleDiscussionRepository.deleteById(salleId);
    }
}