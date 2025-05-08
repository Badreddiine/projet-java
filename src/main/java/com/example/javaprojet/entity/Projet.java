package com.example.javaprojet.entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class Projet {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nomCourt;

    private String nomLong;

    private String description;

    private String theme;

    private String type;

    private boolean estPublic;

    private String license;

    private String etat;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCloture;

    @ManyToMany
    @JoinTable(
            name = "projet_utilisateur",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "projet_admin",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> admins = new HashSet<>();

    // Ajout d'une référence explicite à l'admin principal
    @ManyToOne
    @JoinColumn(name = "admin_principal_id")
    private Utilisateur admin;

    // Liste des utilisateurs ayant demandé à rejoindre le projet
    @ManyToMany
    @JoinTable(
            name = "projet_demandeurs",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> demandeursEnAttente = new HashSet<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private Set<Tache> taches = new HashSet<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private Set<Reunion> reunions = new HashSet<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private Set<DepotDocument> depotDocuments = new HashSet<>();

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private Set<ListeDiffusion> listesDiffusion = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL)
    private Set<SalleDiscussion> sallesDiscussion = new HashSet<>();
}
