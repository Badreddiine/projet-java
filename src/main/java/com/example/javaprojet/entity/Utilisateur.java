package com.example.javaprojet.entity;
import java.util.*;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.enums.RoleSecondaire;
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



}
