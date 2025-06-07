package com.example.javaprojet.services;

import com.example.javaprojet.dto.CalendrierDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.CalendrierRepository;
import com.example.javaprojet.repo.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CalendrierService {
    private final CalendrierRepository calendrierRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * Méthode pour créer un calendrier
     * @param dto le calendrier qui vient du front-end
     * @return CalendrierDTO l'objet calendrier créé converti en DTO
     */
    public CalendrierDTO createCalendrier(CalendrierDTO dto, Long userId) {
        log.info("Creating calendrier for user ID: {}", userId);

        try {
            // Validate input
            if (dto == null) {
                throw new IllegalArgumentException("CalendrierDTO cannot be null");
            }
            if (userId == null) {
                throw new IllegalArgumentException("User ID cannot be null");
            }

            // Find user
            Utilisateur proprietaire = utilisateurRepository.findById(userId)
                    .orElseThrow(() -> {
                        log.error("User not found with ID: {}", userId);
                        return new EntityNotFoundException("Utilisateur non trouvé avec l'ID: " + userId);
                    });

            log.info("Found user: {}", proprietaire.getId());

            // Create calendrier entity
            Calendrier calendrier = new Calendrier();
            calendrier.setNom(dto.getNom());
            calendrier.setEstPartage(dto.isEstPartage());
            calendrier.setProprietaire(proprietaire);

            log.info("Saving calendrier: {}", calendrier.getNom());

            // Save to database
            Calendrier saved = calendrierRepository.save(calendrier);

            log.info("Calendrier saved successfully with ID: {}", saved.getId());

            // Convert to DTO and return
            CalendrierDTO calendrierDTO = new CalendrierDTO(saved);
            return calendrierDTO;

        } catch (EntityNotFoundException e) {
            log.error("Entity not found: {}", e.getMessage());
            throw e;
        } catch (DataAccessException e) {
            log.error("Database error while creating calendrier: {}", e.getMessage());
            throw new RuntimeException("Erreur lors de la sauvegarde du calendrier", e);
        } catch (Exception e) {
            log.error("Unexpected error creating calendrier", e);
            throw new RuntimeException("Erreur inattendue lors de la création du calendrier", e);
        }
    }

    /**
     * Retourne un calendrier par son ID
     * @param id l'identifiant du calendrier
     * @return Optional<CalendrierDTO> le calendrier trouvé ou vide
     */
    @Transactional(readOnly = true)
    public Optional<CalendrierDTO> findCalendrierById(Long id) {
        return calendrierRepository.findById(id)
                .map(CalendrierDTO::new);
    }

    /**
     * Retourne tous les calendriers
     * @return List<CalendrierDTO> la liste de tous les calendriers
     */
    @Transactional(readOnly = true)
    public List<CalendrierDTO> findAllCalendrier() {
        return calendrierRepository.findAll()
                .stream()
                .map(CalendrierDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Méthode pour mettre à jour un calendrier
     * @param calendrierDTO les données modifiées du front-end sous format DTO
     * @return CalendrierDTO le calendrier mis à jour
     * @throws ChangeSetPersister.NotFoundException si le calendrier n'existe pas
     */
    public CalendrierDTO updateCalendrier( CalendrierDTO calendrierDTO) throws ChangeSetPersister.NotFoundException {
        Long id = calendrierDTO.getId();
        Calendrier calendrier = calendrierRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);


        calendrier.setNom(calendrierDTO.getNom());
        calendrier.setEstPartage(calendrierDTO.isEstPartage());

        Calendrier updatedCalendrier = calendrierRepository.save(calendrier);
        return new CalendrierDTO(updatedCalendrier);
    }

    /**
     * Méthode pour supprimer un calendrier par son ID
     * @param id l'identifiant du calendrier à supprimer
     * @throws ChangeSetPersister.NotFoundException si le calendrier n'existe pas
     */
    public void deleteCalendrier(Long id) throws ChangeSetPersister.NotFoundException {
        if (!calendrierRepository.existsById(id)) {
            throw new ChangeSetPersister.NotFoundException();
        }
        calendrierRepository.deleteById(id);
    }
}
