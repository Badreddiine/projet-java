package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.entity.Evenement;
import com.example.javaprojet.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
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
