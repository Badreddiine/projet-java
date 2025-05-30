package com.example.javaprojet.entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.example.javaprojet.dto.GroupeDTO;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Groupe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    private boolean estSysteme;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "groupe_utilisateur",
            joinColumns = @JoinColumn(name = "groupe_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    @JsonIgnore
    @OneToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinColumn(name = "groupe_id")
    private Set<Projet> projets = new HashSet<>();

    public Groupe(GroupeDTO groupeDTO) {
        setId(groupeDTO.getId());
        setNom(groupeDTO.getNom());
        setDescription(groupeDTO.getDescription());
        setEstSysteme(groupeDTO.isEstSysteme());
        setDateCreation(groupeDTO.getDateCreation());
        setMembres(new HashSet<>());
        setProjets(new HashSet<>());
    }
}



