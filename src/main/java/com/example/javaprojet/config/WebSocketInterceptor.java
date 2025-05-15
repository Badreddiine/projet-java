package com.example.javaprojet.config;
import com.example.javaprojet.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

@Component
@RequiredArgsConstructor
@Slf4j
public class WebSocketInterceptor implements ChannelInterceptor {
    private PresenceTracker presenceTracker;
    private final UtilisateurService utilisateurService;

    // Utilisation de @Lazy pour éviter la dépendance circulaire
    @Autowired
    public WebSocketInterceptor(UtilisateurService utilisateurService, @Lazy PresenceTracker presenceTracker) {
        this.utilisateurService = utilisateurService;
        this.presenceTracker = presenceTracker;
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        if (accessor != null && accessor.getCommand() != null) {
            Principal principal = accessor.getUser();

            if (principal != null) {
                Long userId = Long.parseLong(principal.getName());

                switch (accessor.getCommand()) {
                    case CONNECT:
                        log.info("Utilisateur connecté: {}", userId);
                        presenceTracker.userConnected(userId);
                        break;

                    case DISCONNECT:
                        log.info("Utilisateur déconnecté: {}", userId);
                        presenceTracker.userDisconnected(userId);
                        break;

                    case SEND:
                        // Mise à jour de l'activité à chaque envoi de message
                        presenceTracker.updateUserActivity(userId);
                        break;

                    default:
                        break;
                }
            }
        }

        return message;
    }


}

