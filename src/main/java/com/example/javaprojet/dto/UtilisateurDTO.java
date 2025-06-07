package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.enums.RoleSecondaire;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import java.util.Date;
import java.util.List;
import java.util.Set;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurDTO {
    private Long id;
    private String identifiant;
    private String nom;
    private String prenom;
    private String email;
    private byte[] photoProfile;
    private String avatar;
    private boolean actif;
    private boolean estEnLigne;
    private boolean estConnecte;
    private Date derniereConnexion;
    private String motDePasse;
    private RoleType role;
    private RoleSecondaire roleSecondaire;

    // OAuth2 fields
    private String providerId;
    private String provider;


    private String refreshToken;

    private Date dateInscription;
    private Date dateCreation;
    private Date dateModification;
    private Long projetId;    // ID du projet concerné
    private Long adminId;



    public UtilisateurDTO(Utilisateur utilisateur) {
      setId(utilisateur.getId());
      setIdentifiant(utilisateur.getIdentifiant());
      setNom(utilisateur.getNom());
      setPrenom(utilisateur.getPrenom());
      setEmail(utilisateur.getEmail());
      setPhotoProfile(utilisateur.getPhotoProfile());
      setAvatar(utilisateur.getAvatar());
      setActif(utilisateur.isActif());
      setEstEnLigne(utilisateur.isEstEnLigne());
      setEstConnecte(utilisateur.isEstConnecte());
      setDerniereConnexion(utilisateur.getDerniereConnexion());
      setMotDePasse(utilisateur.getMotDePasse());
      setRole(utilisateur.getRole());
      setRoleSecondaire(utilisateur.getRoleSecondaire());
      setProviderId(utilisateur.getProviderId());
      setProvider(utilisateur.getProvider());
      setRefreshToken(utilisateur.getRefreshToken());
      setDateInscription(utilisateur.getDateInscription());



        // Mapper dateInscription vers dateCreation
        if (utilisateur.getDateInscription() != null) {
            this.dateCreation = utilisateur.getDateInscription();
        }
    }

    // Constructor for OAuth2 registration
    public UtilisateurDTO(String nom, String prenom, String email, String provider, String providerId) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.provider = provider;
        this.providerId = providerId;
        this.role = RoleType.GUEST;
        this.roleSecondaire = RoleSecondaire.GUESS;
        this.estEnLigne = false;
        this.estConnecte = false;
        this.actif = true;
        this.dateCreation = new Date();
        this.dateInscription = new Date();
    }

    // Constructor for basic user creation
    public UtilisateurDTO(String identifiant, String nom, String prenom, String email) {
        this.identifiant = identifiant;
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = RoleType.GUEST;
        this.roleSecondaire = RoleSecondaire.GUESS;
        this.estEnLigne = false;
        this.estConnecte = false;
        this.actif = true;
        this.dateCreation = new Date();
        this.dateInscription = new Date();
    }

    // **CONSTRUCTEUR POUR LES OPERATIONS DE PROJET**
    public UtilisateurDTO(Long id, Long projetId, Long adminId) {
        this.id = id;
        this.projetId = projetId;
        this.adminId = adminId;
    }

    // Helper method to check if password is provided (for updates)
    public boolean hasPassword() {
        return motDePasse != null && !motDePasse.trim().isEmpty();
    }

    // Helper method to get full name
    public String getFullName() {
        return (prenom != null ? prenom + " " : "") + (nom != null ? nom : "");
    }

    // Helper method to check if user is admin
    public boolean isAdmin() {
        return RoleType.ADMIN.equals(role);
    }

    // Helper method to check if user is project admin
    public boolean isProjectAdmin() {
        return RoleSecondaire.ADMIN_PROJET.equals(roleSecondaire);
    }

    // Helper method to check if user is guest
    public boolean isGuest() {
        return RoleType.GUEST.equals(role);
    }

    // Helper method to check if user is active and connected
    public boolean isActiveAndConnected() {
        return actif && estConnecte;
    }

    // Helper method to check if user uses OAuth2
    public boolean isOAuth2User() {
        return provider != null && !provider.trim().isEmpty() &&
                providerId != null && !providerId.trim().isEmpty();
    }

    // Helper method to get display name (with fallback)
    public String getDisplayName() {
        String fullName = getFullName();
        if (fullName != null && !fullName.trim().isEmpty()) {
            return fullName;
        }
        if (identifiant != null && !identifiant.trim().isEmpty()) {
            return identifiant;
        }
        return email;
    }

    // Method to update connection status
    public void setConnected(boolean connected) {
        this.estConnecte = connected;
        this.estEnLigne = connected;
        if (connected) {
            this.derniereConnexion = new Date();
        }
    }

//    // Method to prepare for JSON serialization (security)
//    public UtilisateurDTO forJsonResponsea {
//        UtilisateurDTO dto = new UtilisateurDTO();
//        dto.id = this.id;
//        dto.identifiant = this.identifiant;
//        dto.nom = this.nom;
//        dto.prenom = this.prenom;
//        dto.email = this.email;
//        dto.avatar = this.avatar;
//        dto.actif = this.actif;
//        dto.estEnLigne = this.estEnLigne;
//        dto.estConnecte = this.estConnecte;
//        dto.derniereConnexion = this.derniereConnexion;
//        dto.role = this.role;
//        dto.roleSecondaire = this.roleSecondaire;
//        dto.provider = this.provider;
//        dto.dateCreation = this.dateCreation;
//        dto.dateModification = this.dateModification;
//        // Ne pas inclure: motDePasse, photoProfile, providerId, refreshToken
//        return dto;
//    }

    // **METHODE POUR VALIDATION DES CHAMPS PROJET**
    public boolean isValidForProjectOperation() {
        return id != null && projetId != null;
    }

    // **METHODE POUR VALIDATION DES OPERATIONS ADMIN**
    public boolean isValidForAdminOperation() {
        return isValidForProjectOperation() && adminId != null;
    }

    @Override
    public String toString() {
        return "UtilisateurDTO{" +
                "id=" + id +
                ", identifiant='" + identifiant + '\'' +
                ", nom='" + nom + '\'' +
                ", prenom='" + prenom + '\'' +
                ", email='" + email + '\'' +
                ", role=" + role +
                ", roleSecondaire=" + roleSecondaire +
                ", actif=" + actif +
                ", estConnecte=" + estConnecte +
                ", estEnLigne=" + estEnLigne +
                ", provider='" + provider + '\'' +
                ", dateCreation=" + dateCreation +
                ", projetId=" + projetId +
                ", adminId=" + adminId +
                '}';
    }

    public void setConnecte(boolean connecte) {
        this.estConnecte = connecte;
        this.estEnLigne = connecte;
    }
}