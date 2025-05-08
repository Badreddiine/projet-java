package com.example.javaprojet.config;

import com.example.javaprojet.entity.Message;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.MessageService;
import com.example.javaprojet.services.SalleDiscussionService;
import com.example.javaprojet.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.SessionDisconnectEvent;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketEventListener {

    private final SimpMessagingTemplate messagingTemplate;
    private final MessageService messageService;
    private final SalleDiscussionService salleDiscussionService;
    private final UtilisateurService utilisateurService;

    @EventListener
    public void handleWebSocketDisconnectListener(SessionDisconnectEvent event) {
        SimpMessageHeaderAccessor headerAccessor = SimpMessageHeaderAccessor.wrap(event.getMessage());

        // Récupérer les informations de session
        String username = (String) headerAccessor.getSessionAttributes().get("username");
        Long salleId = (Long) headerAccessor.getSessionAttributes().get("salleId");

        if (username != null && salleId != null) {
            log.info("Utilisateur déconnecté: {} de la salle: {}", username, salleId);

            // Récupérer la salle concernée
            salleDiscussionService.getSalleById(salleId).ifPresent(salle -> {
                // Trouver l'utilisateur par nom
                utilisateurService.findByNom(username).ifPresent(utilisateur -> {
                    // Créer un message de déconnexion
                    Message message = messageService.creerUserLeaveMessage(utilisateur, salle);

                    // Envoyer au topic approprié
                    messagingTemplate.convertAndSend(salle.getTopic(), message);
                });
            });
        }
    }
}
