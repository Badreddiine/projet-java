package com.example.javaprojet.entity;
import com.example.javaprojet.model.RoleSecondaire;
import com.example.javaprojet.model.StatutProjet;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@AllArgsConstructor
@Getter
@Setter
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

    @ManyToMany
    @JoinTable(
            name = "projet_utilisateur",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    // Admin members of the project - this is a many-to-many relationship
    @ManyToMany
    @JoinTable(
            name = "projet_admin",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> admins = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "admin_id")
    private Utilisateur admin;

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

    @ManyToMany
    @JoinTable(
            name = "projet_demandes_en_attente",
            joinColumns = @JoinColumn(name = "projet_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> demandeursEnAttente = new HashSet<>();

    public Projet(Long projetId) {
    }

    public Projet() {
    }

    // Helper methods for admin management - recommended for consistency
    public void setMainAdmin(Utilisateur admin) {
        this.admin = admin;
        if (admin != null) {
            this.admins.add(admin);
        }
    }

    public Utilisateur getMainAdmin() {
        return this.admin;
    }

    public StatutProjet getStatutProjet() {
        return statutProjet;
    }

    public void setStatutProjet(StatutProjet statutProjet) {
        this.statutProjet = statutProjet;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNomCourt() {
        return nomCourt;
    }

    public void setNomCourt(String nomCourt) {
        this.nomCourt = nomCourt;
    }

    public String getNomLong() {
        return nomLong;
    }

    public void setNomLong(String nomLong) {
        this.nomLong = nomLong;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTheme() {
        return theme;
    }

    public void setTheme(String theme) {
        this.theme = theme;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean isEstPublic() {
        return estPublic;
    }

    public void setEstPublic(boolean estPublic) {
        this.estPublic = estPublic;
    }

    public String getLicense() {
        return license;
    }

    public void setLicense(String license) {
        this.license = license;
    }

    public Date getDateAcceptation() {
        return dateAcceptation;
    }

    public void setDateAcceptation(Date dateAcceptation) {
        this.dateAcceptation = dateAcceptation;
    }

    public Date getDateRejet() {
        return dateRejet;
    }

    public void setDateRejet(Date dateRejet) {
        this.dateRejet = dateRejet;
    }

    public Date getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(Date dateCreation) {
        this.dateCreation = dateCreation;
    }

    public Date getDateCloture() {
        return dateCloture;
    }

    public void setDateCloture(Date dateCloture) {
        this.dateCloture = dateCloture;
    }

    public Set<Utilisateur> getMembres() {
        return membres;
    }

    public void setMembres(Set<Utilisateur> membres) {
        this.membres = membres;
    }

    public Set<Utilisateur> getAdmins() {
        return admins;
    }

    public void setAdmins(Set<Utilisateur> admins) {
        this.admins = admins;
    }

    public Utilisateur getAdmin() {
        return admin;
    }

    public void setAdmin(Utilisateur admin) {
        this.admin = admin;
    }

    public Set<Tache> getTaches() {
        return taches;
    }

    public void setTaches(Set<Tache> taches) {
        this.taches = taches;
    }

    public Set<Reunion> getReunions() {
        return reunions;
    }

    public void setReunions(Set<Reunion> reunions) {
        this.reunions = reunions;
    }

    public Set<DepotDocument> getDepotDocuments() {
        return depotDocuments;
    }

    public void setDepotDocuments(Set<DepotDocument> depotDocuments) {
        this.depotDocuments = depotDocuments;
    }

    public Set<ListeDiffusion> getListesDiffusion() {
        return listesDiffusion;
    }

    public void setListesDiffusion(Set<ListeDiffusion> listesDiffusion) {
        this.listesDiffusion = listesDiffusion;
    }

    public Groupe getGroupe() {
        return groupe;
    }

    public void setGroupe(Groupe groupe) {
        this.groupe = groupe;
    }

    public Set<SalleDiscussion> getSallesDiscussion() {
        return sallesDiscussion;
    }

    public void setSallesDiscussion(Set<SalleDiscussion> sallesDiscussion) {
        this.sallesDiscussion = sallesDiscussion;
    }

    public Set<Utilisateur> getDemandeursEnAttente() {
        return demandeursEnAttente;
    }

    public void setDemandeursEnAttente(Set<Utilisateur> demandeursEnAttente) {
        this.demandeursEnAttente = demandeursEnAttente;
    }
}