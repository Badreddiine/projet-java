package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Groupe;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupeDTO {


        private Long id;
        private String nom;
        private String description;
        private boolean estSysteme;
        private Date dateCreation;
        private List<UtilisateurDTO> membres; // Only include essential member info

        public GroupeDTO(Groupe groupe) {
            setId(groupe.getId());
           setNom(groupe.getNom());
           setDescription(groupe.getDescription());
           setEstSysteme(groupe.isEstSysteme());
           setDateCreation(groupe.getDateCreation());


            // Only include basic member info to avoid circular references
            if (groupe.getMembres() != null) {
                this.membres = groupe.getMembres().stream()
                        .map(UtilisateurDTO::new)
                        .collect(Collectors.toList());
            }
        }

}
