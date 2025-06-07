package com.example.javaprojet.dto;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Reunion;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReunionDTO {


    private Long id;

    private String titre;

    private String description;

    private Date date;

    private String lienMeet;

    private int duree;
    @JsonIgnore
    private Projet projet;

    private boolean estObligatoire;
    public ReunionDTO(Reunion reunion) {
        setId(reunion.getId());
        setTitre(reunion.getTitre());
        setDescription(reunion.getDescription());
        setDate(reunion.getDate());
        setLienMeet(reunion.getLienMeet());
        setDuree(reunion.getDuree());
        setEstObligatoire(reunion.isEstObligatoire());
        setProjet(reunion.getProjet());

    }

}
