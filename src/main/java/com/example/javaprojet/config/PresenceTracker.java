package com.example.javaprojet.config;

import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.UtilisateurRepository;
import com.example.javaprojet.services.UtilisateurService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j

public class PresenceTracker {

    private final UtilisateurService utilisateurService;
    private final SimpMessagingTemplate messagingTemplate;

    private  UtilisateurRepository utilisateurRepository;

    public PresenceTracker(UtilisateurService utilisateurService, SimpMessagingTemplate messagingTemplate, UtilisateurRepository utilisateurRepository) {
        this.utilisateurService = utilisateurService;
        this.messagingTemplate = messagingTemplate;
        this.utilisateurRepository = utilisateurRepository;
    }

    // Map pour stocker les utilisateurs connectés et leur dernière activité
    private final Map<Long, Long> connectedUsers = new ConcurrentHashMap<>();

    // Intervalle en millisecondes pour considérer un utilisateur comme inactif (5 minutes)
    private static final long INACTIVE_THRESHOLD = 5 * 60 * 1000;

    // Utilisation de @Autowired + constructeur au lieu de @RequiredArgsConstructor
    @Autowired
    public PresenceTracker(UtilisateurService utilisateurService, @Lazy SimpMessagingTemplate messagingTemplate) {
        this.utilisateurService = utilisateurService;
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Marquer un utilisateur comme connecté
     */
    public void userConnected(Long userId) {
        connectedUsers.put(userId, System.currentTimeMillis());
        updateUserStatus(userId, true);
    }

    /**
     * Marquer un utilisateur comme déconnecté
     */
    public void userDisconnected(Long userId) {
        connectedUsers.remove(userId);
        updateUserStatus(userId, false);
    }

    /**
     * Mettre à jour l'horodatage d'activité d'un utilisateur
     */
    public void updateUserActivity(Long userId) {
        if (connectedUsers.containsKey(userId)) {
            connectedUsers.put(userId, System.currentTimeMillis());
        } else {
            userConnected(userId);
        }
    }

    /**
     * Vérifier périodiquement les utilisateurs inactifs
     */
    @Scheduled(fixedRate = 60000)  // Exécuté toutes les minutes
    public void checkInactiveUsers() {
        long currentTime = System.currentTimeMillis();

        connectedUsers.forEach((userId, lastActivity) -> {
            if (currentTime - lastActivity > INACTIVE_THRESHOLD) {
                log.info("Utilisateur {} inactif depuis plus de {} minutes", userId, INACTIVE_THRESHOLD / 60000);
                userDisconnected(userId);
            }
        });
    }

    /**
     * Mettre à jour le statut de connexion d'un utilisateur en base de données
     */

    private void updateUserStatus(Long userId, boolean connected) {
        try {
            try {
                Utilisateur utilisateur = utilisateurService.getUtilisateurById(userId);
//                Utilisateur utilisateur = utilisateurRepository.findById(userId)
//                        .orElseThrow(() -> new EntityNotFoundException("Utilisateur non trouvé"));

                utilisateur.setConnecte(connected);
                utilisateur.setDerniereConnexion(new Date());
                utilisateurService.saveUtilisateur(utilisateur);

                messagingTemplate.convertAndSend("/topic/status", Map.of(
                        "userId", userId,
                        "status", connected ? "ONLINE" : "OFFLINE",
                        "timestamp", System.currentTimeMillis()
                ));

            } catch (EntityNotFoundException e) {
                log.warn("Utilisateur avec ID {} non trouvé", userId);
            }
        } catch (Exception e) {
            log.error("Erreur lors de la mise à jour du statut de l'utilisateur {}: {}", userId, e.getMessage());
        }
    }

}