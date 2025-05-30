package com.example.javaprojet.dto;

import com.example.javaprojet.entity.SousTache;
import lombok.Data;

import java.util.Date;


@Data
public class SousTacheDTO {
    private Long id;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String etat;
    private boolean terminee;


    public SousTacheDTO(SousTache sousTache) {
        setId(sousTache.getId());
        setTitre(sousTache.getTitre());
        setDescription(sousTache.getDescription());
        setDateDebut(sousTache.getDateDebut());
        setDateFin(sousTache.getDateFin());
        setEtat(sousTache.getEtat());
        setTerminee(sousTache.isTerminee());
    }

}
