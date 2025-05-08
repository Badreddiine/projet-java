package com.example.javaprojet.services;

import com.example.javaprojet.entity.Message;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.MessageType;
import com.example.javaprojet.repo.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MessageService {

    private final MessageRepository messageRepository;

    public Optional<Message> getMessageById(Long id) {
        return messageRepository.findById(id);
    }

    @Transactional
    public Message saveMessage(Message message) {
        return messageRepository.save(message);
    }

    public List<Message> getMessagesBySalle(SalleDiscussion salle) {
        return messageRepository.findBySalleOrderByDateEnvoiAsc(salle);
    }

    public List<Message> getMessagesBySalleId(Long salleId) {
        return messageRepository.findBySalle_IdOrderByDateEnvoiAsc(salleId);
    }

    @Transactional
    public Message creerSystemMessage(String contenu, SalleDiscussion salle) {
        Message message = Message.builder()
                .contenu(contenu)
                .dateEnvoi(new Date())
                .salle(salle)
                .type(MessageType.SYSTEM)
                .estLu(false)
                .build();
        return messageRepository.save(message);
    }

    @Transactional
    public Message creerUserJoinMessage(Utilisateur utilisateur, SalleDiscussion salle) {
        Message message = Message.builder()
                .idExpediteur(utilisateur.getId())
                .expediteur(utilisateur)
                .contenu(utilisateur.getNom() + " a rejoint la salle")
                .dateEnvoi(new Date())
                .salle(salle)
                .type(MessageType.JOIN)
                .estLu(false)
                .build();
        return messageRepository.save(message);
    }

    @Transactional
    public Message creerUserLeaveMessage(Object utilisateur, SalleDiscussion salle) {
        return null;
    }

    @Transactional
    public void marquerCommeLu(Long messageId) {
        messageRepository.findById(messageId).ifPresent(message -> {
            message.setEstLu(true);
            messageRepository.save(message);
        });
    }

    @Transactional
    public void marquerTousCommeLus(Long salleId, Long utilisateurId) {
        List<Message> messages = messageRepository.findByEstLuFalseAndSalle_Id(salleId);
        messages.forEach(message -> {
            // Ne pas marquer ses propres messages comme lus
            if (message.getIdExpediteur() == null || !message.getIdExpediteur().equals(utilisateurId)) {
                message.setEstLu(true);
                messageRepository.save(message);
            }
        });
    }

    @Transactional
    public void supprimerMessage(Long messageId) {
        messageRepository.deleteById(messageId);
    }

    public Message creerUserLeaveMessage(Object utilisateur, SalleDiscussion salle) {
    }
}

