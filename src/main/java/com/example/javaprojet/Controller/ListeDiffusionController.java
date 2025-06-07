package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.ListDiffusionDTO;
import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.services.ListeDiffusionService;
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


    @PostMapping
    public ResponseEntity<ListDiffusionDTO> createListeDiffusion(@RequestBody @Valid ListDiffusionDTO dto) {

        if (dto.getId() != null) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
        ListeDiffusion listeDiffusi= new ListeDiffusion(dto);
        ListeDiffusion listeDiffusionCreated=listeDiffusionService.create(listeDiffusi);
        ListDiffusionDTO listeDiffusionDTO = new ListDiffusionDTO(listeDiffusionCreated);
        return ResponseEntity.status(HttpStatus.CREATED).body(listeDiffusionDTO);
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
