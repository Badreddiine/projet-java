package com.example.javaprojet.entity;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.*;

@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class DepotDocument extends Ressource {

    private boolean estPublic;

    @ManyToOne
    @JoinColumn(name = "id_projet")
    private Projet projet;

    public boolean isEstPublic() {
        return estPublic;
    }

    public void setEstPublic(boolean estPublic) {
        this.estPublic = estPublic;
    }

    public Projet getProjet() {
        return projet;
    }

    public void setProjet(Projet projet) {
        this.projet = projet;
    }

}