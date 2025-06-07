package com.example.javaprojet.dto;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Tache;
import com.example.javaprojet.entity.Utilisateur;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TacheDTO {

    private Long id;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private int priorite;
    private int difficulte;
    private String etat;
    private int notation;
    private Projet projet;

    // CORRECTION: renommé de "assignerA" vers "assigneA" pour correspondre à l'entité
    @JsonIgnore
    private Utilisateur assigneA;

    public TacheDTO(Tache tache) {
        setId(tache.getId());
        setTitre(tache.getTitre());
        setDescription(tache.getDescription());
        setDateDebut(tache.getDateDebut());
        setDateFin(tache.getDateFin());
        setPriorite(tache.getPriorite());
        setDifficulte(tache.getDifficulte());
        setEtat(tache.getEtat());
        setNotation(tache.getNotation());
        setProjet(tache.getProjet());
        setAssigneA(tache.getAssigneA());
    }
}