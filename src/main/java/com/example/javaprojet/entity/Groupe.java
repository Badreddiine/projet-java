package com.example.javaprojet.entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    private boolean estSysteme;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @ManyToMany
    @JoinTable(
            name = "groupe_utilisateur",
            joinColumns = @JoinColumn(name = "groupe_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    @OneToMany
    @JoinColumn(name = "groupe_id")
    private Set<Projet> projets = new HashSet<>();

    public Long getId() {
        return id;
    }

    public String getNom() {
        return nom;
    }

    public String getDescription() {
        return description;
    }

    public boolean isEstSysteme() {
        return estSysteme;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public Set<Utilisateur> getMembres() {
        return membres;
    }

    public Set<Projet> getProjets() {
        return projets;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setEstSysteme(boolean estSysteme) {
        this.estSysteme = estSysteme;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public void setMembres(Set<Utilisateur> membres) {
        this.membres = membres;
    }

    public void setProjets(Set<Projet> projets) {
        this.projets = projets;
    }
}

