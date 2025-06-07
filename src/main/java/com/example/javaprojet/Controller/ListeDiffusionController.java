package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.ListDiffusionDTO;
import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.services.ListeDiffusionService;
import com.example.javaprojet.services.ProjetService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/ListeDiffusion")
@RequiredArgsConstructor
public class ListeDiffusionController {
    private final ListeDiffusionService listeDiffusionService;
    private final ProjetService projetService;

    @PostMapping
    public ResponseEntity<ListDiffusionDTO> createListeDiffusion(@RequestBody @Valid ListDiffusionDTO dto) {
        if (dto.getId() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }

        Projet projet = projetService.findProjetById(dto.getProjetId());


        // Créer l'objet ListeDiffusion en injectant l'objet Projet attaché
        ListeDiffusion liste = new ListeDiffusion();
        liste.setNom(dto.getNom());
        liste.setProjet(projet); // ✅ entité gérée

        ListeDiffusion saved = listeDiffusionService.create(liste);

        // Mapper vers DTO
        ListDiffusionDTO responseDto = new ListDiffusionDTO(saved);

        return ResponseEntity.status(HttpStatus.CREATED).body(responseDto);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ListDiffusionDTO> getListeDiffusionById(@PathVariable ListDiffusionDTO dto) {
        long id = dto.getId();
        Optional<ListDiffusionDTO> listeDiffusion = listeDiffusionService.findById(id);
        return listeDiffusion.map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    @GetMapping
    public ResponseEntity<List<ListDiffusionDTO>> getListeDiffusion() {
        return ResponseEntity.ok(listeDiffusionService.findAll());

    }
    @PutMapping("/{id}")
    public ResponseEntity<ListDiffusionDTO> updateListeDiffusionById(@PathVariable ListDiffusionDTO dto) {
        try{
            ListDiffusionDTO updated =listeDiffusionService.update(dto);
            return ResponseEntity.ok(updated);
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }
    @DeleteMapping ("/{id}")
    public ResponseEntity<Void> deleteListeDiffusionById(@PathVariable ListDiffusionDTO dto) {
        Long id = dto.getId();
        try{
            listeDiffusionService.delete(id);
            return ResponseEntity.noContent().build();
        }catch (Exception e){
            return ResponseEntity.notFound().build();
        }
    }

}
