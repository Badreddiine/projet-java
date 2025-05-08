package com.example.javaprojet.entity;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import com.example.javaprojet.model.TypeSalle;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalleDiscussion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nom;

    private String description;

    @Enumerated(EnumType.STRING)
    private TypeSalle typeSalle;  // GROUPE, PROJET, PRIVEE, GENERALE

    private boolean estPublique;

    @Temporal(TemporalType.TIMESTAMP)
    private Date dateCreation;

    @ManyToOne
    @JoinColumn(name = "id_projet")
    private Projet projet;

    @ManyToOne
    @JoinColumn(name = "id_groupe")
    private Groupe groupe;

    @OneToMany(mappedBy = "salle", cascade = CascadeType.ALL)
    private Set<Message> messages = new HashSet<>();

    @ManyToMany
    @JoinTable(
            name = "salle_utilisateur",
            joinColumns = @JoinColumn(name = "salle_id"),
            inverseJoinColumns = @JoinColumn(name = "utilisateur_id")
    )
    private Set<Utilisateur> membres = new HashSet<>();

    @ManyToOne
    @JoinColumn(name = "createur_id")
    private Utilisateur createur;

    // Méthode utilitaire pour vérifier si une salle est privée entre deux utilisateurs
    public boolean estSessionPrivee() {
        return typeSalle == TypeSalle.PRIVEE && membres.size() == 2;
    }

    // Méthode pour obtenir le topic WebSocket correspondant à cette salle
    public String getTopic() {
        switch (typeSalle) {
            case GROUPE:
                return "/topic/groupe/" + groupe.getId();
            case PROJET:
                return "/topic/projet/" + projet.getId();
            case PRIVEE:
                return "/topic/prive/" + id;
            case GENERALE:
                return "/topic/public";
            default:
                return "/topic/salle/" + id;
        }
    }
}
