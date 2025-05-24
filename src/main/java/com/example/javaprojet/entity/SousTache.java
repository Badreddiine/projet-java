package com.example.javaprojet.entity;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import jakarta.persistence.*;
import lombok.Data;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Data

public class SousTache {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private String description;

    @ElementCollection
    @CollectionTable(name = "sous_tache_tags", joinColumns = @JoinColumn(name = "sous_tache_id"))
    @Column(name = "tag")
    private List<String> tags=new ArrayList<>();

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    private String etat;

    private boolean estTerminee;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_tache")
    private Tache tache;

}
