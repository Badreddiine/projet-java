package com.example.javaprojet.entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import com.example.javaprojet.dto.SousTacheDTO;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SousTache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private String description;

    @JsonIgnore
    @ElementCollection
    @CollectionTable(name = "sous_tache_tags", joinColumns = @JoinColumn(name = "sous_tache_id"))
    @Column(name = "tag")
    private List<String> tags=new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    private String etat;

    private boolean terminee;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tache")
    private Tache tache;

    public SousTache(SousTacheDTO sousTacheDTO) {
       setId(sousTacheDTO.getId());
       setTitre(sousTacheDTO.getTitre());
       setDescription(sousTacheDTO.getDescription());
       setDateDebut(sousTacheDTO.getDateDebut());
       setDateFin(sousTacheDTO.getDateFin());
       setEtat(sousTacheDTO.getEtat());
       setTerminee(sousTacheDTO.isTerminee());
       setTags(new ArrayList<>());
    }
}
