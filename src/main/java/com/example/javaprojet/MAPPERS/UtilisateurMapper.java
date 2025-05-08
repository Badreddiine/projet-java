package com.example.javaprojet.MAPPERS;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Utilisateur;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurMapper {

    public UtilisateurDTO toDTO(Utilisateur utilisateur) {
        if (utilisateur == null) {
            return null;
        }

        return UtilisateurDTO.builder()
                .id(utilisateur.getId())
                .nom(utilisateur.getNom())
                .avatar(utilisateur.getAvatar())
                .estEnLigne(utilisateur.isEstConnecte())
                .build();
    }

    // Nouvelle méthode pour créer un DTO avec token JWT
    public UtilisateurDTO toDTOWithToken(Utilisateur utilisateur, String token, String refreshToken) {
        UtilisateurDTO dto = toDTO(utilisateur);

        return dto;
    }
}