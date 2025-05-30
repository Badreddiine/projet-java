package com.example.javaprojet.dto;
import com.example.javaprojet.entity.Reunion;
import lombok.Data;
import java.util.Date;
@Data
public class ReunionDTO {


    private Long id;

    private String titre;

    private String description;

    private Date date;

    private String lienMeet;

    private int duree;

    private boolean estObligatoire;
    public ReunionDTO(Reunion reunion) {
        setId(reunion.getId());
        setTitre(reunion.getTitre());
        setDescription(reunion.getDescription());
        setDate(reunion.getDate());
        setLienMeet(reunion.getLienMeet());
        setDuree(reunion.getDuree());
        setEstObligatoire(reunion.isEstObligatoire());

    }

}
