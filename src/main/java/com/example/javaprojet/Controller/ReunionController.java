package com.example.javaprojet.Controller;


import com.example.javaprojet.entity.Reunion;
import com.example.javaprojet.services.ReunionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/reunions")
public class ReunionController {

    private final ReunionService reunionService;

    @Autowired
    public ReunionController(ReunionService reunionService) {
        this.reunionService = reunionService;
    }

    @PostMapping("/projet/{projetId}")
    public Reunion createReunion(@RequestBody Reunion reunion, @PathVariable Long projetId) {
        return reunionService.createWithProjet(reunion, projetId);
    }

    @PostMapping("/{reunionId}/rejoindre/{utilisateurId}")
    public Reunion rejoindreReunion(@PathVariable Long reunionId, @PathVariable Long utilisateurId) {
        return reunionService.ajouterParticipant(reunionId, utilisateurId);
    }

    @GetMapping
    public List<Reunion> getAll() {
        return reunionService.findAll();
    }

    @GetMapping("/{id}")
    public Reunion getById(@PathVariable Long id) {
        return reunionService.findById(id)
                .orElseThrow(() -> new RuntimeException("Réunion non trouvée avec id " + id));
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        reunionService.delete(id);
    }
}
