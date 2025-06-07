package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.enums.StatutProjet;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.Date;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProjetDTO {
    private Long id;
    private Long createurId;  // Correction: camelCase au lieu de CreateurId
    private Long groupeId;    // Correction: camelCase au lieu de GroupeId
    private Long utilisateurId; // Ajouté pour les opérations de mise à jour/suppression
    private Long adminId;     // Ajouté pour les opérations d'administration
    private String nomCourt;
    private String nomLong;
    private String description;
    private String theme;
    private String type;
    private boolean estPublic;
    private String license;
    private StatutProjet statutProjet;
    private Date dateAcceptation;
    private Date dateRejet;
    private Date dateCreation;
    private Date dateCloture;

    // Constructeur à partir d'un objet Projet
    public ProjetDTO(Projet projet) {
        this.id = projet.getId();
        this.nomCourt = projet.getNomCourt();
        this.nomLong = projet.getNomLong();
        this.description = projet.getDescription();
        this.theme = projet.getTheme();
        this.type = projet.getType();
        this.estPublic = projet.isEstPublic();
        this.license = projet.getLicense();
        this.statutProjet = projet.getStatutProjet();
        this.dateAcceptation = projet.getDateAcceptation();
        this.dateRejet = projet.getDateRejet();
        this.dateCreation = projet.getDateCreation();
        this.dateCloture = projet.getDateCloture();

        // Vérification de null pour éviter les NullPointerException
        if (projet.getMAinAdmin() != null) {
            this.createurId = projet.getMAinAdmin().getId();
        }

        if (projet.getGroupe() != null) {
            this.groupeId = projet.getGroupe().getId();
        }
    }
}