package com.example.javaprojet.entity;

import java.util.HashSet;
import java.util.Set;

import com.example.javaprojet.dto.ListDiffusionDTO;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ListeDiffusion extends Ressource {

    private String description;

    private boolean estSysteme;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projet")
    private Projet projet;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "liste_diffusion_utilisateur",
            joinColumns = @JoinColumn(name = "liste_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> abonnes = new HashSet<>();

    public ListeDiffusion(ListDiffusionDTO listDiffusionDTO) {
        setId(listDiffusionDTO.getId());
        setType(listDiffusionDTO.getType());
        setChemin(listDiffusionDTO.getChemin());
        setNom(listDiffusionDTO.getNom());
        setDescription(listDiffusionDTO.getDescription());
        setEstSysteme(listDiffusionDTO.isEstSysteme());
        setDescription(listDiffusionDTO.getDescription());
        setEstSysteme(listDiffusionDTO.isEstSysteme());
        setAbonnes(new HashSet<>());
        setAbonnes(new HashSet<>());
    }

}
