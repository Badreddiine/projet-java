package com.example.javaprojet.entity;

import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.enums.RoleSecondaire;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;

@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@NoArgsConstructor
@AllArgsConstructor
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String identifiant;

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = true)
    private String motDePasse;

    @Lob
    private byte[] photoProfile;

    private String avatar;
    private boolean actif;
    private boolean estConnecte;
    private boolean estEnLigne;

    @Temporal(TemporalType.TIMESTAMP)
    private Date derniereConnexion;

    @Enumerated(EnumType.STRING)
    private RoleType role = RoleType.USER;

    @Enumerated(EnumType.STRING)
    private RoleSecondaire roleSecondaire = RoleSecondaire.GUESS;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateInscription;

    private String providerId;
    private String provider;

    @Column(length = 1000)
    private String refreshToken;
    @JsonIgnore
    @JsonBackReference
    @ManyToMany(mappedBy = "membres", fetch = FetchType.LAZY)
    private Set<Groupe> groupes = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "membres", fetch = FetchType.LAZY)
    private Set<Projet> projets = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "admins", fetch = FetchType.LAZY)
    private Set<Projet> projetsAdministres = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "expediteur", fetch = FetchType.LAZY)
    private List<Message> messagesEnvoyes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "destinataire", fetch = FetchType.LAZY)
    private List<Message> messagesRecus = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "proprietaire", fetch = FetchType.LAZY)
    private Set<Calendrier> calendriers = new HashSet<>();

    public void setConnecte(boolean connecte) {
        this.estConnecte = connecte;
        this.estEnLigne = connecte;
    }

    @Override
    public String toString() {
        return "Utilisateur{" +
                "id=" + id +
                ", identifiant='" + identifiant + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", actif=" + actif +
                ", estConnecte=" + estConnecte +
                ", derniereConnexion=" + derniereConnexion +
                ", dateInscription=" + dateInscription +
                ", provider='" + provider + '\'' +
                '}';
    }

    public Utilisateur(UtilisateurDTO dto) {
        this.id = dto.getId();
        this.identifiant = dto.getIdentifiant();
        this.nom = dto.getNom();
        this.prenom = dto.getPrenom();
        this.email = dto.getEmail();
        this.motDePasse = dto.getMotDePasse();
        this.photoProfile = dto.getPhotoProfile();
        this.avatar = dto.getAvatar();
        this.actif = dto.isActif();
        this.estConnecte = dto.isEstConnecte();
        this.estEnLigne = dto.isEstEnLigne();
        this.derniereConnexion = dto.getDerniereConnexion();
        this.role = dto.getRole();
        this.roleSecondaire = dto.getRoleSecondaire();
        this.dateInscription = dto.getDateInscription();
        this.providerId = dto.getProviderId();
        this.provider = dto.getProvider();
        this.refreshToken = dto.getRefreshToken();

        this.groupes = new HashSet<>();
        this.projets = new HashSet<>();
        this.projetsAdministres = new HashSet<>();
        this.messagesEnvoyes = new ArrayList<>();
        this.messagesRecus = new ArrayList<>();
        this.calendriers = new HashSet<>();
    }

    @Override
    public int hashCode() {
        return Objects.hash(id); // Utilisez seulement l'ID pour le hash
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Utilisateur that = (Utilisateur) o;
        return Objects.equals(id, that.id); // Comparez seulement par ID
    }
}
