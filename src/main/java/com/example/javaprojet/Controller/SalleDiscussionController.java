package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.SalleDiscussionDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
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
import java.util.Optional;
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
        UtilisateurDTO utilisateur = utilisateurService.getUtilisateurById(userId);

        List<SalleDiscussionDTO> salles = salleDiscussionService.getSallesUtilisateur(utilisateur).stream()
                .map(salle -> salleMapper.toDTO(salle, userId))  // Use the mapper instead of constructor
                .collect(Collectors.toList());

        return ResponseEntity.ok(salles);
    }

    @GetMapping("/{salleId}")
    public ResponseEntity<SalleDiscussionDTO> getSalleById(@PathVariable Long salleId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        Optional<SalleDiscussionDTO> salleOpt = salleDiscussionService.getSalleById(salleId);
        SalleDiscussion salle = new SalleDiscussion(salleOpt.get());
        if (salleOpt.isPresent()) {
            SalleDiscussionDTO salleDTO = salleMapper.toDTO(salle, userId);
            return ResponseEntity.ok(salleDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<SalleDiscussionDTO> creerSalle(@RequestBody SalleDiscussionDTO salleDTO, Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        SalleDiscussion nouvelleSalle = salleDiscussionService.creerSalle(salleDTO, userId);
        SalleDiscussionDTO resultat = salleMapper.toDTO(nouvelleSalle, userId);

        return ResponseEntity.ok(resultat);
    }

    @PostMapping("/{salleId}/rejoindre")
    public ResponseEntity<Void> rejoindreSalle(@PathVariable Long salleId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        salleDiscussionService.ajouterMembre(salleId, userId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{salleId}/quitter")
    public ResponseEntity<Void> quitterSalle(@PathVariable Long salleId, Principal principal) {
        Long userId = Long.parseLong(principal.getName());

        salleDiscussionService.retirerMembre(salleId, userId);
        return ResponseEntity.ok().build();
    }
}