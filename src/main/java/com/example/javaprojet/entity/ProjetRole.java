package com.example.javaprojet.entity;

import com.example.javaprojet.model.RoleSecondaire;
import com.example.javaprojet.model.RoleType;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class ProjetRole {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Utilisateur utilisateur;

    @ManyToOne
    private Projet projet;

    @Enumerated(EnumType.STRING)
    private RoleType role;
    @Enumerated(EnumType.STRING)
    private RoleSecondaire roleSecondaire;

    public ProjetRole(Projet projet, Utilisateur demandeur, RoleSecondaire roleSecondaire) {
        this.projet = projet;
        this.utilisateur = demandeur;
        this.roleSecondaire = roleSecondaire;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }
}
