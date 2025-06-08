package com.example.javaprojet.entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.example.javaprojet.dto.EvenementDTO;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Evenement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String titre;

    private String description;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateDebut;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateFin;

    private String lieu;

    private boolean estRecurrent;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_calendrier")
    private Calendrier calendrier;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY,cascade = CascadeType.ALL)
    @JoinTable(
            name = "evenement_utilisateur",
            joinColumns = @JoinColumn(name = "evenement_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> participants = new HashSet<>();
    public Evenement(EvenementDTO evenementDto) {
        setId(evenementDto.getId());
        setTitre(evenementDto.getTitre());
        setDescription(evenementDto.getDescription());
        setDateDebut(evenementDto.getDateDebut());
        setDateFin(evenementDto.getDateFin());
        setLieu(evenementDto.getLieu());
        setEstRecurrent(evenementDto.isEstRecurrent());
        setCalendrier(null);
        setParticipants(new HashSet<>());
    }
}