package com.example.javaprojet.MAPPERS;
import com.example.javaprojet.dto.MessageDTO;
import com.example.javaprojet.entity.Message;
import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageDTO toDTO(Message message) {
        if (message == null) {
            return null;
        }

        return MessageDTO.builder()
                .id(message.getIdMessage())
                .contenu(message.getContenu())
                .dateEnvoi(message.getDateEnvoi())
                .estLu(message.isEstLu())
                .type(message.getType())
                .idExpediteur(message.getIdExpediteur())
                .nomExpediteur(message.getExpediteur() != null ? message.getExpediteur().getNom() : null)
                .idSalle(message.getSalle() != null ? message.getSalle().getId() : null)
                .nomSalle(message.getSalle() != null ? message.getSalle().getNom() : null)
                .build();
    }

    public Message toEntity(MessageDTO dto) {
        if (dto == null) {
            return null;
        }

        return Message.builder()
                .idMessage(dto.getId())
                .contenu(dto.getContenu())
                .dateEnvoi(dto.getDateEnvoi())
                .estLu(dto.isEstLu())
                .type(dto.getType())
                .idExpediteur(dto.getIdExpediteur())
                .build();
    }
}

