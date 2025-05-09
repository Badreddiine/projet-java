package com.example.javaprojet.Controller;
import com.example.javaprojet.entity.Message;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.MessageService;
import com.example.javaprojet.services.SalleDiscussionService;
import com.example.javaprojet.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageController {

    private final MessageService messageService;
    private final SalleDiscussionService salleDiscussionService;
    private final UtilisateurService utilisateurService;

    @GetMapping("/{id}")
    public ResponseEntity<Message> getMessageById(@PathVariable Long id) {
        return messageService.getMessageById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/salle/{salleId}")
    public ResponseEntity<List<Message>> getMessagesBySalleId(@PathVariable Long salleId) {
        return ResponseEntity.ok(messageService.getMessagesBySalleId(salleId));
    }

    @PostMapping
    public ResponseEntity<Message> createMessage(@RequestBody Message message) {
        return ResponseEntity.status(HttpStatus.CREATED).body(messageService.saveMessage(message));
    }

    @PostMapping("/system")
    public ResponseEntity<Message> createSystemMessage(@RequestBody Map<String, Object> payload) {
        Long salleId = Long.valueOf(payload.get("salleId").toString());
        String contenu = (String) payload.get("contenu");

        return salleDiscussionService.getSalleById(salleId)
                .map(salle -> {
                    Message message = messageService.creerSystemMessage(contenu, salle);
                    return ResponseEntity.status(HttpStatus.CREATED).body(message);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/join")
    public ResponseEntity<Message> createJoinMessage(@RequestBody Map<String, Object> payload) {
        Long salleId = Long.valueOf(payload.get("salleId").toString());
        Long userId = Long.valueOf(payload.get("userId").toString());

        SalleDiscussion salle = salleDiscussionService.getSalleById(salleId)
                .orElse(null);
        Utilisateur utilisateur = utilisateurService.findById(userId)
                .orElse(null);

        if (salle == null || utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        Message message = messageService.creerUserJoinMessage(utilisateur, salle);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PostMapping("/leave")
    public ResponseEntity<Message> createLeaveMessage(@RequestBody Map<String, Object> payload) {
        Long salleId = Long.valueOf(payload.get("salleId").toString());
        Long userId = Long.valueOf(payload.get("userId").toString());

        SalleDiscussion salle = salleDiscussionService.getSalleById(salleId)
                .orElse(null);
        Utilisateur utilisateur = utilisateurService.findById(userId)
                .orElse(null);

        if (salle == null || utilisateur == null) {
            return ResponseEntity.notFound().build();
        }

        Message message = messageService.creerUserLeaveMessage(utilisateur, salle);
        return ResponseEntity.status(HttpStatus.CREATED).body(message);
    }

    @PutMapping("/marquer-lu/{id}")
    public ResponseEntity<Void> marquerCommeLu(@PathVariable Long id) {
        messageService.marquerCommeLu(id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/marquer-tous-lus")
    public ResponseEntity<Void> marquerTousCommeLus(@RequestParam Long salleId, @RequestParam Long utilisateurId) {
        messageService.marquerTousCommeLus(salleId, utilisateurId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMessage(@PathVariable Long id) {
        messageService.supprimerMessage(id);
        return ResponseEntity.ok().build();
    }
}