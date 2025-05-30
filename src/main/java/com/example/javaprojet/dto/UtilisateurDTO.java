package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Utilisateur;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UtilisateurDTO {
    private Long id;
    private String nom;
    private String prenom;
    private String email;
    private String avatar;
    private boolean estEnLigne;

    public UtilisateurDTO(Utilisateur utilisateur) {
        setId(utilisateur.getId());
        setNom(utilisateur.getNom());
        setPrenom(utilisateur.getPrenom());
        setEmail(utilisateur.getEmail());
        setAvatar(utilisateur.getAvatar());
        setEstEnLigne(utilisateur.isEstEnLigne());
    }
}
