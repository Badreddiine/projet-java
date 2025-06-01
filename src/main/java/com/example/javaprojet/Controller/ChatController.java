package com.example.javaprojet.Controller;
import com.example.javaprojet.dto.MessageDTO;
import com.example.javaprojet.dto.SalleDiscussionDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
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

        // 1. Récupérer l'ID de l'utilisateur connecté
        Long userId = Long.parseLong(principal.getName());
        UtilisateurDTO utilisateur = utilisateurService.getUtilisateurById(userId);

        // 2. Vérifier que la salle existe
        Optional<SalleDiscussionDTO> salleOpt = salleDiscussionService.getSalleById(salleId);
        if (salleOpt.isEmpty()) {
            // Optionnel : logger ou gérer le cas où la salle n'existe pas
            return;
        }

        SalleDiscussionDTO salle = salleOpt.get();
        SalleDiscussion salleDiscussion = new SalleDiscussion(salle);

        // 3. Vérifier que l'utilisateur est membre de la salle
        if (!salle.getMembres().contains(utilisateur)) {
            // Optionnel : logger ou gérer l'accès refusé
            return;
        }

        // 4. Créer l'entité Message
        Utilisateur uti = new Utilisateur(utilisateur);
        Message message = Message.builder()
                .contenu(messageDTO.getContenu())
                .idExpediteur(userId)
                .expediteur(uti)
                .dateEnvoi(new Date())
                .salle(salleDiscussion)
                .estLu(false)
                .type(MessageType.CHAT)
                .build();

        // 5. Sauvegarder le message
        Message savedMessage = messageService.saveMessage(message);

        // 6. Convertir en DTO
        MessageDTO sentMessageDTO = messageMapper.toDTO(savedMessage);

        // 7. Envoyer le message aux abonnés de la salle
        messagingTemplate.convertAndSend(salle.getTopic(), sentMessageDTO);
    }
}

