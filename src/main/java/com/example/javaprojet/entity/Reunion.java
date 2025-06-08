package com.example.javaprojet.entity;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.example.javaprojet.dto.ReunionDTO;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.BatchSize;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Reunion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    @NotBlank(message = "Le titre est obligatoire")
    @Size(max = 255, message = "Le titre ne peut pas dépasser 255 caractères")
    private String titre;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    @NotNull(message = "La date est obligatoire")
    private Date date;

    @Size(max = 500, message = "Le lien Meet ne peut pas dépasser 500 caractères")
    private String lienMeet;

    @Min(value = 1, message = "La durée doit être d'au moins 1 minute")
    private int duree;

    private boolean estObligatoire;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_projet")
    private Projet projet;

    @JsonIgnore
    @BatchSize(size = 10)
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "reunion_utilisateur",
            joinColumns = @JoinColumn(name = "reunion_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> participants = new HashSet<>();

    /**
     * Constructeur à partir d'un DTO
     * @param reunionDto le DTO de réunion
     */
    public Reunion(ReunionDTO reunionDto) {
        if (reunionDto != null) {
            this.titre = reunionDto.getTitre();
            this.description = reunionDto.getDescription();
            this.date = reunionDto.getDate();
            this.lienMeet = reunionDto.getLienMeet();
            this.duree = reunionDto.getDuree();
            this.estObligatoire = reunionDto.isEstObligatoire();
            this.participants = new HashSet<>();
        }
    }

    /**
     * Ajoute un participant à la réunion
     * @param utilisateur l'utilisateur à ajouter
     */
    public void ajouterParticipant(Utilisateur utilisateur) {
        if (utilisateur != null) {
            this.participants.add(utilisateur);
        }
    }

    /**
     * Supprime un participant de la réunion
     * @param utilisateur l'utilisateur à supprimer
     */
    public void supprimerParticipant(Utilisateur utilisateur) {
        if (utilisateur != null) {
            this.participants.remove(utilisateur);
        }
    }
}