package com.example.javaprojet.dto;

import com.example.javaprojet.enums.MessageType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MessageDTO {
    private Long id;
    private String contenu;
    private Date dateEnvoi;
    private boolean estLu;
    private MessageType type;
    private Long idExpediteur;
    private String nomExpediteur;
    private String avatarExpediteur;
    private Long idSalle;
    private String nomSalle;
}

