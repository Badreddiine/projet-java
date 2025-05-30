package com.example.javaprojet.dto;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Tache;
import lombok.Data;
import java.util.Date;
@Data
public class TacheDTO {

    private Long id;
    private String titre;
    private String description;
    private Date dateDebut;

    private Date dateFin;

    private int priorite;

    private int difficulte;

    private String etat;

    private int notation;

    private Projet projet;

  public TacheDTO(Tache tache) {
      setId(tache.getId());
      setTitre(tache.getTitre());
      setDescription(tache.getDescription());
      setDateDebut(tache.getDateDebut());
      setDateFin(tache.getDateFin());
      setPriorite(tache.getPriorite());
      setDifficulte(tache.getDifficulte());
      setEtat(tache.getEtat());
      setNotation(tache.getNotation());
      setProjet(tache.getProjet());
  }
}
