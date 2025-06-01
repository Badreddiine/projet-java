package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Evenement;
import lombok.Data;

import java.util.Date;

@Data
public class EvenementDTO {

    private Long id;
    private String titre;
    private String description;
    private Date dateDebut;
    private Date dateFin;
    private String lieu;
    private boolean estRecurrent;
    private Long calendrierId;

    public EvenementDTO(Evenement evenement) {
        setId(evenement.getId());
        setTitre(evenement.getTitre());
        setDescription(evenement.getDescription());
        setDateDebut(evenement.getDateDebut());
        setDateFin(evenement.getDateFin());
        setLieu(evenement.getLieu());
        setEstRecurrent(evenement.isEstRecurrent());
        setCalendrierId(evenement.getCalendrier().getId());
    }
}
