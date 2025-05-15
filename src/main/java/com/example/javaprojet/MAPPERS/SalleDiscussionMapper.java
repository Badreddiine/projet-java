package com.example.javaprojet.MAPPERS;

import com.example.javaprojet.dto.SalleDiscussionDTO;
import com.example.javaprojet.entity.Message;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.repo.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class SalleDiscussionMapper {

    private final UtilisateurMapper utilisateurMapper;
    private final MessageMapper messageMapper;
    private final MessageRepository messageRepository;

    public SalleDiscussionDTO toDTO(SalleDiscussion salle, Long utilisateurId) {
        if (salle == null) {
            return null;
        }

        // Récupérer le dernier message
        List<Message> messages = messageRepository.findBySalleOrderByDateEnvoiAsc(salle);
        Message dernierMessage = messages.isEmpty() ? null :
                messages.stream().max(Comparator.comparing(Message::getDateEnvoi)).orElse(null);

        // Compter les messages non lus pour cet utilisateur
        long messagesNonLus = messages.stream()
                .filter(m -> !m.isEstLu() && (m.getIdExpediteur() == null || !m.getIdExpediteur().equals(utilisateurId)))
                .count();

        return SalleDiscussionDTO.builder()
                .id(salle.getId())

                .description(salle.getDescription())
                .typeSalle(salle.getTypeSalle())
                .estPublique(salle.isEstPublique())
                .dateCreation(salle.getDateCreation())
                .idProjet(salle.getProjet() != null ? salle.getProjet().getId() : null)
                .nomProjet(salle.getProjet() != null ? salle.getProjet().getNomCourt() : null)
                .idGroupe(salle.getGroupe() != null ? salle.getGroupe().getId() : null)
                .nomGroupe(salle.getGroupe() != null ? salle.getGroupe().getNom() : null)
                .idCreateur(salle.getCreateur() != null ? salle.getCreateur().getId() : null)
                .nomCreateur(salle.getCreateur() != null ? salle.getCreateur().getNom() : null)
                .membres(salle.getMembres().stream()
                        .map(utilisateurMapper::toDTO)
                        .collect(Collectors.toList()))
                .dernierMessage(messageMapper.toDTO(dernierMessage))
                .nombreMessagesNonLus((int) messagesNonLus)
                .build();
    }
}


