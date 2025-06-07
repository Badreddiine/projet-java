package com.example.javaprojet.entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.example.javaprojet.dto.TacheDTO;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
public class Tache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private String description;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSX")
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    private int priorite;

    private int difficulte;

    private String etat;

    private int notation;

    @JsonIgnore
    @JsonIgnoreProperties({"taches", "admins", "admin", "reunions", "depotDocuments", "listesDiffusion", "groupe", "sallesDiscussion", "demandeursEnAttente", "mainAdmin"})
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projet")
    private Projet projet;

    @JsonIgnore
    @OneToMany(mappedBy = "tache", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<SousTache> sousTaches = new HashSet<>();

    @JsonIgnore
    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_utilisateur")
    private Utilisateur assigneA;

    public Tache(TacheDTO tacheDTO) {
        setTitre(tacheDTO.getTitre());
        setDescription(tacheDTO.getDescription());
        setDateDebut(tacheDTO.getDateDebut());
        setDateFin(tacheDTO.getDateFin());
        setPriorite(tacheDTO.getPriorite());
        setDifficulte(tacheDTO.getDifficulte());
        setEtat(tacheDTO.getEtat());
        setNotation(tacheDTO.getNotation());
        setProjet(tacheDTO.getProjet());
        setAssigneA(tacheDTO.getAssigneA());
          setPriorite(tacheDTO.getPriorite());
        setSousTaches(new HashSet<>());
    }
}