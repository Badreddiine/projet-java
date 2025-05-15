package com.example.javaprojet.entity;
import com.example.javaprojet.model.MessageType;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
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

    @ManyToOne
    @JoinColumn(name = "id_expediteur", insertable = false, updatable = false)
    private Utilisateur expediteur;

    @Column(name = "id_expediteur")
    private Long idExpediteur;

    @ManyToOne
    @JoinColumn(name = "id_salle")
    private SalleDiscussion salle;
    @ManyToOne
    @JoinColumn(name = "id_destinataire")
    private Utilisateur destinataire;
}




