package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.enums.StatutProjet;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Data
@Getter
@Setter
public class ProjetDTO {
    private Long id;
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

    public ProjetDTO(Projet projet) {
        setId(projet.getId());
        setNomCourt(projet.getNomCourt());
        setNomLong(projet.getNomLong());
        setDescription(projet.getDescription());
        setTheme(projet.getTheme());
        setType(projet.getType());
        setEstPublic(projet.isEstPublic());
        setLicense(projet.getLicense());
        setStatutProjet(projet.getStatutProjet());
        setDateAcceptation(projet.getDateAcceptation());
        setDateRejet(projet.getDateRejet());
        setDateCreation(projet.getDateCreation());
        setDateCloture(projet.getDateCloture());
    }
}
