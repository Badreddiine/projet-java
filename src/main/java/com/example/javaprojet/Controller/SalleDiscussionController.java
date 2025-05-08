package com.example.javaprojet.Controller;
import com.example.javaprojet.dto.SalleDiscussionDTO;
import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.MAPPERS.SalleDiscussionMapper;
import com.example.javaprojet.services.GroupeService;
import com.example.javaprojet.services.ProjetService;
import com.example.javaprojet.services.SalleDiscussionService;
import com.example.javaprojet.services.UtilisateurService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/salles")
@RequiredArgsConstructor
public class SalleDiscussionController {

    private final SalleDiscussionService salleDiscussionService;
    private final UtilisateurService utilisateurService;
    private final ProjetService projetService;
    private final GroupeService groupeService;
    private final SalleDiscussionMapper salleMapper;

    @GetMapping
    public ResponseEntity<List<SalleDiscussionDTO>> getMesSalles(Principal principal) {
        Long userId = Long.parseLong(principal.getName());
        Utilisateur utilisateur = utilisateurService.getUtilisateurById(userId);

        List<SalleDiscussionDTO> salles = salleDiscussionService.getSallesUtilisateur(utilisateur).stream()
                .map(salle -> salleMapper.toDTO(salle, userId))
                .collect(Collectors.toList());

        return ResponseEntity.ok(salles);
    }

    @GetMapping("/{salleId}")
    public ResponseEntity<SalleDiscussionDTO> getSalleById(@PathVariable Long salleId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        return salleDiscussionService.getSalleById(salleId)
                .map(salle -> ResponseEntity.ok(salleMapper.toDTO(salle, userId)))
                .orElse(ResponseEntity.notFound().build());
    }
}