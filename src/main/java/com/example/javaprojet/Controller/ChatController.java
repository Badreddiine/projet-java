package com.example.javaprojet.Controller;
import com.example.javaprojet.dto.MessageDTO;
import com.example.javaprojet.entity.Message;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.MAPPERS.MessageMapper;
import com.example.javaprojet.enums.MessageType;
import com.example.javaprojet.services.MessageService;
import com.example.javaprojet.services.SalleDiscussionService;
import com.example.javaprojet.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Controller;


import java.security.Principal;
import java.util.Date;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class ChatController {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final UtilisateurService utilisateurService;
    private final SalleDiscussionService salleDiscussionService;
    private final MessageMapper messageMapper;

    @MessageMapping("/chat.sendMessage/{salleId}")
    public void sendMessage(@Payload MessageDTO messageDTO,
                            @DestinationVariable Long salleId,
                            Principal principal) {

        Long userId = Long.parseLong(principal.getName());
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(userId);

        Optional<SalleDiscussion> salleOpt = salleDiscussionService.getSalleById(salleId);
        if (salleOpt.isPresent()) {
            SalleDiscussion salle = salleOpt.get();

            // Vérifier si l'utilisateur est membre de la salle
            if (salle.getMembres().contains(utilisateur)) {
                // Créer et préparer l'entité Message
                Message message = Message.builder()
                        .contenu(messageDTO.getContenu())
                        .idExpediteur(userId)
                        .expediteur(utilisateur)
                        .dateEnvoi(new Date())
                        .salle(salle)
                        .estLu(false)
                        .type(MessageType.CHAT)
                        .build();

                // Persister le message
                Message savedMessage = messageService.saveMessage(message);

                // Convertir en DTO pour l'envoi
                MessageDTO sentMessageDTO = messageMapper.toDTO(savedMessage);

                // Envoyer au topic approprié
                messagingTemplate.convertAndSend(salle.getTopic(), sentMessageDTO);
            }
        }
    }
}

