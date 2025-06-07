package com.example.javaprojet.Controller;
import com.example.javaprojet.dto.MessageDTO;
import com.example.javaprojet.entity.Message;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.MessageType;
import com.example.javaprojet.services.MessageService;
import com.example.javaprojet.services.SalleDiscussionService;
import com.example.javaprojet.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

    @MessageMapping("/chat.sendMessage/{salleId}")
    public void sendMessage(@Payload MessageDTO messageDTO,
                            @DestinationVariable Long salleId,
                            Principal principal) {
        try {
            // Validate and parse user ID
            Long userId = parseUserId(principal);
            if (userId == null) {
                return;
            }

            // Get user
            Utilisateur utilisateur = utilisateurService.getUtilisateurById(userId);
            if (utilisateur==null) {
                return;
            }


            // Get chat room
            Optional<SalleDiscussion> salleOpt = salleDiscussionService.getSalleById(salleId);
            if (salleOpt.isEmpty()) {
                return;
            }

            SalleDiscussion salle = salleOpt.get();

            // Check if user is member of the room
            if (!salle.getMembres().contains(utilisateur)) {
                return;
            }

            // Create and prepare Message entity
            Message message = Message.builder()
                    .contenu(messageDTO.getContenu())
                    .idExpediteur(userId)
                    .expediteur(utilisateur)
                    .dateEnvoi(new Date())
                    .salle(salle)
                    .estLu(false)
                    .type(MessageType.CHAT)
                    .build();

            // Persist the message
            Message savedMessage = messageService.saveMessage(message);

            // Convert to DTO for sending
            MessageDTO sentMessageDTO = MessageDTO.fromEntity(savedMessage);

            // Send to appropriate topic
            messagingTemplate.convertAndSend(salle.getTopic(), sentMessageDTO);

        } catch (Exception e) {
        }
    }

    private Long parseUserId(Principal principal) {
        try {
            return Long.parseLong(principal.getName());
        } catch (NumberFormatException e) {
            return null;
        }
    }
}