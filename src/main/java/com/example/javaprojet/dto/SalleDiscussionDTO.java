package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.enums.TypeSalle;
import lombok.*;

import java.util.Date;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalleDiscussionDTO {
    private Long id;
    private String nom;
    private String description;
    private TypeSalle typeSalle;  // GROUPE, PROJET, PRIVEE, GENERALE
    private boolean estPublique;
    private Date dateCreation;

    // Full objects (if needed for some use cases)
    private Projet projet;
    private Groupe groupe;

    // Additional fields for the mapper
    private Long idProjet;
    private String nomProjet;
    private Long idGroupe;
    private String nomGroupe;
    private Long idCreateur;
    private String nomCreateur;
    private List<UtilisateurDTO> membres;
    private MessageDTO dernierMessage;
    private int nombreMessagesNonLus;

    // Constructor from entity (simplified version)
    public SalleDiscussionDTO(SalleDiscussion salleDiscussion) {
        setId(salleDiscussion.getId());
        setNom(salleDiscussion.getNom());
        setDescription(salleDiscussion.getDescription());
        setTypeSalle(salleDiscussion.getTypeSalle());
        setEstPublique(salleDiscussion.isEstPublique());
        setDateCreation(salleDiscussion.getDateCreation());
        setProjet(salleDiscussion.getProjet());
        setGroupe(salleDiscussion.getGroupe());

        // Set additional fields
        if (salleDiscussion.getProjet() != null) {
            setIdProjet(salleDiscussion.getProjet().getId());
            setNomProjet(salleDiscussion.getProjet().getNomCourt());
        }
        if (salleDiscussion.getGroupe() != null) {
            setIdGroupe(salleDiscussion.getGroupe().getId());
            setNomGroupe(salleDiscussion.getGroupe().getNom());
        }
        if (salleDiscussion.getCreateur() != null) {
            setIdCreateur(salleDiscussion.getCreateur().getId());
            setNomCreateur(salleDiscussion.getCreateur().getNom());
        }
    }

    // Méthode pour obtenir le topic WebSocket correspondant à cette salle
    public String getTopic() {
        switch (typeSalle) {
            case GROUPE:
                return "/topic/groupe/" + (groupe != null ? groupe.getId() : idGroupe);
            case PROJET:
                return "/topic/projet/" + (projet != null ? projet.getId() : idProjet);
            case PRIVEE:
                return "/topic/prive/" + id;
            case GENERALE:
                return "/topic/public";
            default:
                return "/topic/salle/" + id;
        }
    }
}