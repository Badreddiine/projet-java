package com.example.javaprojet.Controller;

import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.services.CalendrierService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/calendriers")
public class CalendrierController {

    private final CalendrierService calendrierService;

    @Autowired
    public CalendrierController(CalendrierService calendrierService) {
        this.calendrierService = calendrierService;
    }

    @PostMapping
    public ResponseEntity<Calendrier> createCalendrier(@RequestBody Calendrier calendrier) {
        return new ResponseEntity<>(calendrierService.create(calendrier), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Calendrier> getCalendrierById(@PathVariable Long id) {
        return calendrierService.findById(id)
                .map(calendrier -> new ResponseEntity<>((Calendrier) calendrier, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping
    public ResponseEntity<List<Calendrier>> getAllCalendriers() {
        List<Calendrier> calendriers = (List<Calendrier>) calendrierService.findAll();
        return new ResponseEntity<>(calendriers, HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Calendrier> updateCalendrier(@PathVariable Long id, @RequestBody Calendrier calendrier) {
        try {
            Calendrier updated = calendrierService.update(id, calendrier);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendrier(@PathVariable Long id) {
        try {
            calendrierService.delete(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (RuntimeException e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
