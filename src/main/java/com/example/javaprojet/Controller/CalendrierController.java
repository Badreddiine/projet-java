package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.CalendrierDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.CalendrierService;
import com.example.javaprojet.services.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/calendriers")
public class CalendrierController {

    private final CalendrierService calendrierService;
    private final UtilisateurService utilisateurService;

    @Autowired
    public CalendrierController(CalendrierService calendrierService, UtilisateurService utilisateurService) {
        this.calendrierService = calendrierService;
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/")
    public ResponseEntity<CalendrierDTO> createCalendrier(Principal principal, @RequestBody @Valid CalendrierDTO calendrierDTO) {
        UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByEmail(principal.getName());
        if (utilisateur != null) {
            Utilisateur uti=new Utilisateur(utilisateur);
            Calendrier calendrier = calendrierService.create(calendrierDTO, uti);
            return new ResponseEntity<>(new CalendrierDTO(calendrier), HttpStatus.CREATED);
        } else {
            // This condition is unaccessible
            return new ResponseEntity<>(calendrierDTO, HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<CalendrierDTO> getCalendrierById(@PathVariable Long id) {
        Calendrier calendrier = calendrierService.findById(id);
        if (calendrier != null) {
            return new ResponseEntity<>(new CalendrierDTO(calendrier), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // TODO : Maybe this needs to change
    @PutMapping("/")
    public ResponseEntity<CalendrierDTO> updateCalendrier(Principal principal, @RequestBody CalendrierDTO calendrierDTO) {
        UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByEmail(principal.getName());
        if (calendrierDTO.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        boolean hasCalendar = utilisateurService.hasCalendar(utilisateur.getId(), calendrierDTO.getId());
        if (hasCalendar) {
            Calendrier calendrier = calendrierService.update(calendrierDTO);
            if (calendrier != null) {
                return new ResponseEntity<>(new CalendrierDTO(calendrier), HttpStatus.OK);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCalendrier(Principal principal, @PathVariable Long id) {
        UtilisateurDTO utilisateur = utilisateurService.getUtilisateurByEmail(principal.getName());
        if (utilisateurService.hasCalendar(utilisateur.getId(), id)) {
            calendrierService.delete(id);
            return ResponseEntity.ok().build();
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
