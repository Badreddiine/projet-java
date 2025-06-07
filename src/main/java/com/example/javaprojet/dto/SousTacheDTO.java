package com.example.javaprojet.dto;

import com.example.javaprojet.entity.SousTache;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;


@Data

@NoArgsConstructor
@AllArgsConstructor
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
