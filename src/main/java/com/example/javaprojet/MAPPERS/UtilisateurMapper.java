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
                .build();
    }
}
