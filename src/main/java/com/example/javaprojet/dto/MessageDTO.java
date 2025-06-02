package com.example.javaprojet.dto;

import com.example.javaprojet.entity.Message;
import com.example.javaprojet.enums.MessageType;
import lombok.*;

import java.util.Date;

@Getter
@Setter
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

    // Copy constructor
    public MessageDTO(MessageDTO messageDTO) {
        setId(messageDTO.getId());
        setContenu(messageDTO.getContenu());
        setDateEnvoi(messageDTO.getDateEnvoi());
        setEstLu(messageDTO.isEstLu());
        setType(messageDTO.getType());
        setIdExpediteur(messageDTO.getIdExpediteur());
        setNomExpediteur(messageDTO.getNomExpediteur());
        setAvatarExpediteur(messageDTO.getAvatarExpediteur());
        setIdSalle(messageDTO.getIdSalle());
        setNomSalle(messageDTO.getNomSalle());
    }

    // Static factory method to create DTO from Entity
    public static MessageDTO fromEntity(Message message) {
        return MessageDTO.builder()
                .id(message.getIdMessage())
                .contenu(message.getContenu())
                .dateEnvoi(message.getDateEnvoi())
                .estLu(message.isEstLu())
                .type(message.getType())
                .idExpediteur(message.getIdExpediteur())
                .nomExpediteur(message.getExpediteur() != null ? message.getExpediteur().getNom() : null)
                .avatarExpediteur(message.getExpediteur() != null ? message.getExpediteur().getAvatar() : null)
                .idSalle(message.getSalle() != null ? message.getSalle().getId() : null)
                .nomSalle(message.getSalle() != null ? message.getSalle().getNom() : null)
                .build();
    }

    // Instance method to create DTO from Entity (alternative approach)
    public MessageDTO(Message message) {
        this.id = message.getIdMessage();
        this.contenu = message.getContenu();
        this.dateEnvoi = message.getDateEnvoi();
        this.estLu = message.isEstLu();
        this.type = message.getType();
        this.idExpediteur = message.getIdExpediteur();
        this.nomExpediteur = message.getExpediteur() != null ? message.getExpediteur().getNom() : null;
        this.avatarExpediteur = message.getExpediteur() != null ? message.getExpediteur().getAvatar() : null;
        this.idSalle = message.getSalle() != null ? message.getSalle().getId() : null;
        this.nomSalle = message.getSalle() != null ? message.getSalle().getNom() : null;
    }
}