package com.example.javaprojet.Controller;
import com.example.javaprojet.entity.DepotDocument;
import com.example.javaprojet.services.DepotDocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/documents")
public class DepotDocumentController {

    private final DepotDocumentService depotDocumentService;

    @Autowired
    public DepotDocumentController(DepotDocumentService depotDocumentService) {
        this.depotDocumentService = depotDocumentService;
    }

    @PostMapping
    public ResponseEntity<DepotDocument> createDocument(@RequestBody DepotDocument document) {
        DepotDocument created = depotDocumentService.create(document);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepotDocument> getDocumentById(@PathVariable Long id) {
        return depotDocumentService.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping
    public ResponseEntity<List<DepotDocument>> getAllDocuments() {
        List<DepotDocument> documents = depotDocumentService.findAll();
        return ResponseEntity.ok(documents);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DepotDocument> updateDocument(@PathVariable Long id, @RequestBody DepotDocument document) {
        try {
            DepotDocument updated = depotDocumentService.update(id, document);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id) {
        try {
            depotDocumentService.delete(id);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }
}
