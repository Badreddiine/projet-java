package com.example.javaprojet.services;

import com.example.javaprojet.dto.SalleDiscussionDTO;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.TypeSalle;
import com.example.javaprojet.repo.SalleDiscussionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
@RequiredArgsConstructor
@Transactional
public class SalleDiscussionService {

    private final SalleDiscussionRepository salleDiscussionRepository;

    /**
     * methode pour trouver une salle de discution par sans id
     * @param id de salle de discution
     * @return
     */
    public Optional<SalleDiscussion> getSalleById(Long id) {

        return salleDiscussionRepository.findById(id);
    }

    /**
     *methode pour cree une salle de discution de groupe
     * @param salleDiscussionDTO
     * @param groupe il faut   chenger le dto recuperer dans front end par entite dans controlleur
     * @param createur il faut   chenger le dto recuperer dans front end par entite dans controlleur
     * @return
     */
    public SalleDiscussion creerSalleGroupe(SalleDiscussionDTO salleDiscussionDTO, Groupe groupe, Utilisateur createur) {

        SalleDiscussion salle = SalleDiscussion.builder()
                .nom(salleDiscussionDTO.getNom())
                .description(salleDiscussionDTO.getDescription())
                .typeSalle(TypeSalle.GROUPE)
                .estPublique(true)
                .dateCreation(new Date())
                .groupe(groupe)
                .createur(createur)
                .membres(new HashSet<>(groupe.getMembres()))
                .build();
        return salleDiscussionRepository.save(salle);
    }

    /**
     * methode pour creer une salle de projet
     * @param salleDiscussionDTO
     * @param projet il faut   chenger le dto recuperer dans front end par entite dans controlleur
     * @param createur il faut   chenger le dto recuperer dans front end par entite dans controlleur
     * @return
     */
    public SalleDiscussion creerSalleProjet(SalleDiscussionDTO salleDiscussionDTO, Projet projet, Utilisateur createur) {
        SalleDiscussion salle = SalleDiscussion.builder()
                .nom(salleDiscussionDTO.getNom())
                .description(salleDiscussionDTO.getDescription())
                .typeSalle(TypeSalle.PROJET)
                .estPublique(true)
                .dateCreation(new Date())
                .projet(projet)
                .createur(createur)
                .membres(new HashSet<>(projet.getMembres()))
                .build();
        return salleDiscussionRepository.save(salle);
    }

    /**
     * methode  pour cree une salle de discutiojn generale
     * @param salleDiscussionDTO
     * @param createur il faut   chenger le dto recuperer dans front end par entite dans controlleur
     * @return
     */
    public SalleDiscussion creerSalleGenerale(SalleDiscussionDTO salleDiscussionDTO, Utilisateur createur) {
        SalleDiscussion salle = SalleDiscussion.builder()
                .nom(salleDiscussionDTO.getNom())
                .description(salleDiscussionDTO.getDescription())
                .typeSalle(TypeSalle.GENERALE)
                .estPublique(true)
                .dateCreation(new Date())
                .createur(createur)
                .membres(new HashSet<>())
                .build();
        return salleDiscussionRepository.save(salle);
    }

    /**
     * methode pour cree une salle de discution privee
     * @param utilisateur1 il faut   chenger le dto recuperer dans front end par entite dans controlleur
     * @param utilisateur2 il faut   chenger le dto recuperer dans front end par entite dans controlleur
     * @return
     */
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

    /**
     *  pour recuperrer tout les salle cree par un utilisateur
     * @param utilisateur il faut   chenger le dto recuperer dans front end par entite dans controlleur
     * @return
     */
    public List<SalleDiscussion> getSallesUtilisateur(Utilisateur utilisateur) {
        return salleDiscussionRepository.findByMembresContaining(utilisateur);
    }


    /**
     * pour recuperer la salle de projet
     * @param projetId soit recuperer dans front end et envoyer soit recuperer du dtio envoyer
     * @return
     */
    public List<SalleDiscussion> getSallesProjet(Long projetId) {
        return salleDiscussionRepository.findByProjet_Id(projetId);
    }

    /**
     * pour recuperer la  salle de projet
     * @param groupeId soit recuperer dans front end et envoyer soit recuperer du dtio envoyer
     * @return
     */
    public List<SalleDiscussion> getSallesGroupe(Long groupeId) {
        return salleDiscussionRepository.findByGroupe_Id(groupeId);
    }

    /**
     *  ajouter membre a une salle de discution
     * @param salle changer le dto dans controller et changer par entite
     * @param utilisateur changer le dto dans controller et changer par entite
     */
    public void ajouterUtilisateur(SalleDiscussion salle, Utilisateur utilisateur) {
        salle.getMembres().add(utilisateur);
        salleDiscussionRepository.save(salle);
    }

    /**
     *  suorimer un utilisateur d une salle de discution
     * @param salle changer le dto dans controller et changer par entite
     * @param utilisateur changer le dto dans controller et changer par entite
     */
    public void supprimerUtilisateur(SalleDiscussion salle, Utilisateur utilisateur) {
        salle.getMembres().remove(utilisateur);
        salleDiscussionRepository.save(salle);
    }

    /**
     * method epour suprimer une salle par son id
     * @param salleId soit on recuperes dans front end ou d une dto envoyer de front end
     */
    public void supprimerSalle(Long salleId) {
        salleDiscussionRepository.deleteById(salleId);
    }
}

