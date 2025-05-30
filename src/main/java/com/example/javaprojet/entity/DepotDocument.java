package com.example.javaprojet.entity;
import com.example.javaprojet.dto.DepotDocumentDTO;
import jakarta.persistence.*;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

@Entity
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Data
public class DepotDocument extends Ressource {

    private boolean estPublic;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "id_projet")
    private Projet projet;

    public DepotDocument(DepotDocumentDTO depotDocumentDTO) {
        setProjet(depotDocumentDTO.getProjet());
        setEstPublic(depotDocumentDTO.isEstPublic());
    }
}