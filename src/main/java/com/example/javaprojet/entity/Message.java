package com.example.javaprojet.entity;
import com.example.javaprojet.model.MessageType;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMessage;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String contenu;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateEnvoi;

    private boolean estLu;

    private Long idDestination;
    private String destination;
    private String typeDestination;

    @Enumerated(EnumType.STRING)
    private MessageType type;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_expediteur", insertable = false, updatable = false)
    private Utilisateur expediteur;

    @Column(name = "id_expediteur")
    private Long idExpediteur;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_salle")
    private SalleDiscussion salle;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_destinataire")
    private Utilisateur destinataire;
}




