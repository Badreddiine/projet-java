package com.example.javaprojet.entity;
import java.util.*;
import com.example.javaprojet.model.RoleType;
import com.example.javaprojet.model.RoleSecondaire;
import com.example.javaprojet.model.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Data
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
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

    // Pour intégrer avec UtilisateurDTO
    private String avatar;

    private boolean actif;

    private boolean estConnecte;
    private boolean estEnLigne; // Pour correspondre au DTO

    @Temporal(TemporalType.TIMESTAMP)
    private Date derniereConnexion;

    @Enumerated(EnumType.STRING)
    private RoleType role;

    @Enumerated(EnumType.STRING)
    private RoleSecondaire roleSecondaire;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateInscription;

    // Ajout pour l'authentification OAuth2
    private String providerId; // ID de l'utilisateur chez le fournisseur (Google)
    private String provider; // Nom du fournisseur (google, local, etc.)

    // Pour le refresh token (JWT)
    @Column(length = 1000)
    private String refreshToken;
    @JsonIgnore
    @ManyToMany(mappedBy = "membres",fetch = FetchType.LAZY)
    private Set<Groupe> groupes = new HashSet<>();

    @ManyToMany(mappedBy = "membres")
    private Set<Projet> projets = new HashSet<>();

    @JsonIgnore
    @ManyToMany(mappedBy = "admins",fetch = FetchType.LAZY)
    private Set<Projet> projetsAdministres = new HashSet<>();

    @JsonIgnore
    @OneToMany(mappedBy = "expediteur",fetch = FetchType.LAZY)
    private List<Message> messagesEnvoyes = new ArrayList<>();

    @JsonIgnore
    @OneToMany(mappedBy = "destinataire",fetch = FetchType.LAZY)
    private List<Message> messagesRecus;

    @JsonIgnore
    @OneToMany(mappedBy = "proprietaire",fetch = FetchType.LAZY)
    private Set<Calendrier> calendriers = new HashSet<>();

    // Méthodes utilitaires pour le statut de connexion
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


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIdentifiant() {
        return identifiant;
    }

    public void setIdentifiant(String identifiant) {
        this.identifiant = identifiant;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public byte[] getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(byte[] photoProfile) {
        this.photoProfile = photoProfile;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public boolean isActif() {
        return actif;
    }

    public void setActif(boolean actif) {
        this.actif = actif;
    }

    public boolean isEstConnecte() {
        return estConnecte;
    }

    public void setEstConnecte(boolean estConnecte) {
        this.estConnecte = estConnecte;
    }

    public boolean isEstEnLigne() {
        return estEnLigne;
    }

    public void setEstEnLigne(boolean estEnLigne) {
        this.estEnLigne = estEnLigne;
    }

    public Date getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(Date derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    public RoleType getRole() {
        return role;
    }

    public void setRole(RoleType role) {
        this.role = role;
    }

    public RoleSecondaire getRoleSecondaire() {
        return roleSecondaire;
    }

    public void setRoleSecondaire(RoleSecondaire roleSecondaire) {
        this.roleSecondaire = roleSecondaire;
    }

    public Date getDateInscription() {
        return dateInscription;
    }

    public void setDateInscription(Date dateInscription) {
        this.dateInscription = dateInscription;
    }

    public String getProviderId() {
        return providerId;
    }

    public void setProviderId(String providerId) {
        this.providerId = providerId;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public Set<Groupe> getGroupes() {
        return groupes;
    }

    public void setGroupes(Set<Groupe> groupes) {
        this.groupes = groupes;
    }

    public Set<Projet> getProjets() {
        return projets;
    }

    public void setProjets(Set<Projet> projets) {
        this.projets = projets;
    }

    public Set<Projet> getProjetsAdministres() {
        return projetsAdministres;
    }

    public void setProjetsAdministres(Set<Projet> projetsAdministres) {
        this.projetsAdministres = projetsAdministres;
    }

    public List<Message> getMessagesEnvoyes() {
        return messagesEnvoyes;
    }

    public void setMessagesEnvoyes(List<Message> messagesEnvoyes) {
        this.messagesEnvoyes = messagesEnvoyes;
    }

    public List<Message> getMessagesRecus() {
        return messagesRecus;
    }

    public void setMessagesRecus(List<Message> messagesRecus) {
        this.messagesRecus = messagesRecus;
    }

    public Set<Calendrier> getCalendriers() {
        return calendriers;
    }

    public void setCalendriers(Set<Calendrier> calendriers) {
        this.calendriers = calendriers;
    }

}
