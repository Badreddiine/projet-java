package com.example.javaprojet.entity;

import com.example.javaprojet.dto.GroupeDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Getter
@Setter
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

    // Remove @JsonManagedReference, keep only @JsonIgnore to prevent circular references
    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "groupe_utilisateur",
            joinColumns = @JoinColumn(name = "groupe_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    @JsonIgnore  // Also ignore projets to avoid potential issues
    @OneToMany(fetch = FetchType.EAGER, cascade = CascadeType.ALL)
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