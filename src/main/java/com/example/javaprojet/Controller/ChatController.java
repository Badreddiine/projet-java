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
@Slf4j
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
                log.error("Invalid user ID from principal: {}", principal.getName());
                return;
            }

            // Get user
            Optional<Utilisateur> utilisateurOpt = utilisateurService.getUtilisateurById(userId);
            if (utilisateurOpt.isEmpty()) {
                log.error("User not found with ID: {}", userId);
                return;
            }
            Utilisateur utilisateur = utilisateurOpt.get();

            // Get chat room
            Optional<SalleDiscussion> salleOpt = salleDiscussionService.getSalleById(salleId);
            if (salleOpt.isEmpty()) {
                log.error("Chat room not found with ID: {}", salleId);
                return;
            }

            SalleDiscussion salle = salleOpt.get();

            // Check if user is member of the room
            if (!salle.getMembres().contains(utilisateur)) {
                log.warn("User {} is not a member of chat room {}", userId, salleId);
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
            log.error("Error sending message to room {}: {}", salleId, e.getMessage(), e);
        }
    }

    private Long parseUserId(Principal principal) {
        try {
            return Long.parseLong(principal.getName());
        } catch (NumberFormatException e) {
            log.error("Failed to parse user ID from principal: {}", principal.getName());
            return null;
        }
    }
}