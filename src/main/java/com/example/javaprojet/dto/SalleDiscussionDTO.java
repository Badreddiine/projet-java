package com.example.javaprojet.dto;


import com.example.javaprojet.model.TypeSalle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalleDiscussionDTO {
    private Long id;
    private String nom;
    private String description;
    private TypeSalle typeSalle;
    private boolean estPublique;
    private Date dateCreation;
    private Long idProjet;
    private String nomProjet;
    private Long idGroupe;
    private String nomGroupe;
    private Long idCreateur;
    private String nomCreateur;
    private List<UtilisateurDTO> membres;
    private MessageDTO dernierMessage;
    private int nombreMessagesNonLus;
}

