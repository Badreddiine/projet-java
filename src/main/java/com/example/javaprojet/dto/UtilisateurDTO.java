package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.enums.RoleSecondaire;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
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

    // Security-related fields
    @JsonIgnore // Don't expose password in JSON responses
    private String motDePasse;
    private RoleType role;
    private RoleSecondaire roleSecondaire;

    // OAuth2 fields
    private String providerId;
    private String provider;

    @JsonIgnore
    private String refreshToken;

    // Date tracking
    private Date dateInscription;
    private Date dateCreation;
    private Date dateModification;

    // Constructeur principal : Entité -> DTO
    public UtilisateurDTO(Utilisateur utilisateur) {
        setId(utilisateur.getId());
        setIdentifiant(utilisateur.getIdentifiant());
        setNom(utilisateur.getNom());
        setPrenom(utilisateur.getPrenom());
        setEmail(utilisateur.getEmail());
        setMotDePasse(utilisateur.getMotDePasse());
        setPhotoProfile(utilisateur.getPhotoProfile());
        setAvatar(utilisateur.getAvatar());
        setActif(utilisateur.isActif());
        setEstConnecte(utilisateur.isEstConnecte());
        setEstEnLigne(utilisateur.isEstEnLigne());
        setDerniereConnexion(utilisateur.getDerniereConnexion());
        setRole(utilisateur.getRole());
        setRoleSecondaire(utilisateur.getRoleSecondaire());
        setDateInscription(utilisateur.getDateInscription());
        setProviderId(utilisateur.getProviderId());
        setProvider(utilisateur.getProvider());
        setRefreshToken(utilisateur.getRefreshToken());

        // Mapper dateInscription vers dateCreation
        if (utilisateur.getDateInscription() != null) {
            setDateCreation(utilisateur.getDateInscription());
        }
    }

    // Constructor for creating user with password (for registration)


    // Constructor for OAuth2 registration
    public UtilisateurDTO(String nom, String prenom, String email, String provider, String providerId) {
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setProvider(provider);
        setProviderId(providerId);
        setRole(RoleType.GUEST);
        setRoleSecondaire(RoleSecondaire.GUESS);
        setEstEnLigne(false);
        setEstConnecte(false);
        setActif(true);
        setDateCreation(new Date());
        setDateInscription(new Date());
    }

    // Constructor for basic user creation
    public UtilisateurDTO(String identifiant, String nom, String prenom, String email) {
        setIdentifiant(identifiant);
        setNom(nom);
        setPrenom(prenom);
        setEmail(email);
        setRole(RoleType.GUEST);
        setRoleSecondaire(RoleSecondaire.GUESS);
        setEstEnLigne(false);
        setEstConnecte(false);
        setActif(true);
        setDateCreation(new Date());
        setDateInscription(new Date());
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
        setEstConnecte(connected);
        setEstEnLigne(connected);
        if (connected) {
            setDerniereConnexion(new Date());
        }
    }

    // Method to prepare for JSON serialization (security)
    public UtilisateurDTO forJsonResponse() {
        UtilisateurDTO dto = new UtilisateurDTO();
        dto.setId(this.id);
        dto.setIdentifiant(this.identifiant);
        dto.setNom(this.nom);
        dto.setPrenom(this.prenom);
        dto.setEmail(this.email);
        dto.setAvatar(this.avatar);
        dto.setActif(this.actif);
        dto.setEstEnLigne(this.estEnLigne);
        dto.setEstConnecte(this.estConnecte);
        dto.setDerniereConnexion(this.derniereConnexion);
        dto.setRole(this.role);
        dto.setRoleSecondaire(this.roleSecondaire);
        dto.setProvider(this.provider);
        dto.setDateCreation(this.dateCreation);
        dto.setDateModification(this.dateModification);
        // Ne pas inclure: motDePasse, photoProfile, providerId, refreshToken
        return dto;
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
                '}';
    }
    public void setConnecte(boolean connecte) {
        this.estConnecte = connecte;
        this.estEnLigne = connecte;
    }
}