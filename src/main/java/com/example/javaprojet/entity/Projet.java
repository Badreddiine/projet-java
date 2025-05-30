package com.example.javaprojet.entity;
import com.example.javaprojet.dto.ProjetDTO;
import com.example.javaprojet.model.RoleSecondaire;
import com.example.javaprojet.model.StatutProjet;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
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

    @Enumerated(EnumType.STRING)
    private StatutProjet statutProjet;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateAcceptation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRejet;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCloture;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "projet_utilisateur",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();


    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "projet_admin",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> admins = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "admin_id")
    private Utilisateur admin;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Tache> taches = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<Reunion> reunions = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<DepotDocument> depotDocuments = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<ListeDiffusion> listesDiffusion = new HashSet<>();

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "groupe_id")
    private Groupe groupe;

    @JsonIgnore
    @OneToMany(mappedBy = "projet", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<SalleDiscussion> sallesDiscussion = new HashSet<>();

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "projet_demandes_en_attente",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> demandeursEnAttente = new HashSet<>();


    public void setMainAdmin(Utilisateur admin) {
        this.admin = admin;
        if (admin != null) {
            this.admins.add(admin);
        }
    }

    public Projet(ProjetDTO projetDTO) {
        setId(projetDTO.getId());
        setNomCourt(projetDTO.getNomCourt());
        setNomLong(projetDTO.getNomLong());
        setDescription(projetDTO.getDescription());
        setTheme(projetDTO.getTheme());
        setType(projetDTO.getType());
        setEstPublic(projetDTO.isEstPublic());
        setLicense(projetDTO.getLicense());
        setStatutProjet(projetDTO.getStatutProjet());
        setDateAcceptation(projetDTO.getDateAcceptation());
        setDateRejet(projetDTO.getDateRejet());
        setDateCreation(projetDTO.getDateCreation());
        setDateCloture(projetDTO.getDateCloture());
        setMembres(new HashSet<>());
        setAdmins(new HashSet<>());
        setTaches(new HashSet<>());
        setReunions(new HashSet<>());
        setDepotDocuments(new HashSet<>());
        setListesDiffusion(new HashSet<>());
        setGroupe(null);
        setSallesDiscussion(new HashSet<>());
        setDemandeursEnAttente(new HashSet<>());
    }
}