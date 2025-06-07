package com.example.javaprojet.services;

import com.example.javaprojet.dto.EvenementDTO;
import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.entity.Evenement;
import com.example.javaprojet.repo.CalendrierRepository;
import com.example.javaprojet.repo.EvenementRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class EvenementService {
    private final EvenementRepository evenementRepository;
    private final CalendrierRepository calendrierRepository;

    /**
     * Méthode pour créer un événement avec un calendrier existant
     * @param evenementDTO les données de l'événement
     * @return EvenementDTO l'événement créé converti en DTO
     */
    public EvenementDTO createEvenement(EvenementDTO evenementDTO) {
        // Récupérer le calendrier existant par son ID
        Calendrier calendrier = calendrierRepository.findById(evenementDTO.getCalendrierId())
                .orElseThrow(() -> new EntityNotFoundException("Calendrier non trouvé avec l'ID: " + evenementDTO.getCalendrierId()));

        // Créer l'événement
        Evenement evenement = new Evenement();
        evenement.setTitre(evenementDTO.getTitre());
        evenement.setDescription(evenementDTO.getDescription());
        evenement.setDateDebut(evenementDTO.getDateDebut());
        evenement.setDateFin(evenementDTO.getDateFin());
        evenement.setLieu(evenementDTO.getLieu());
        evenement.setEstRecurrent(evenementDTO.isEstRecurrent());
        evenement.setCalendrier(calendrier);

        Evenement savedEvenement = evenementRepository.save(evenement);
        return new EvenementDTO(savedEvenement);
    }

    /**
     * Méthode pour récupérer un événement par son ID
     * @param id l'identifiant de l'événement
     * @return Optional<EvenementDTO> l'événement trouvé ou vide
     */
    @Transactional(readOnly = true)
    public Optional<EvenementDTO> findEvenementById(Long id) {
        return evenementRepository.findById(id)
                .map(EvenementDTO::new);
    }

    /**
     * Méthode pour récupérer tous les événements
     * @return List<EvenementDTO> la liste de tous les événements
     */
    @Transactional(readOnly = true)
    public List<EvenementDTO> findAllEvenements() {
        List<Evenement> allEvenements = evenementRepository.findAll();
        return allEvenements.stream()
                .map(EvenementDTO::new)
                .collect(Collectors.toList());
    }

    /**
     * Méthode pour modifier les données d'un événement
     * @param evenementDTO le DTO envoyé depuis le front-end
     * @return EvenementDTO l'entité modifiée convertie en DTO
     * @throws ChangeSetPersister.NotFoundException si l'événement n'existe pas
     */
    public EvenementDTO updateEvenement(EvenementDTO evenementDTO) throws ChangeSetPersister.NotFoundException {
        Long id = evenementDTO.getId();
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // Mise à jour des propriétés (le calendrier reste inchangé)
        evenement.setLieu(evenementDTO.getLieu());
        evenement.setDescription(evenementDTO.getDescription());
        evenement.setDateFin(evenementDTO.getDateFin());
        evenement.setDateDebut(evenementDTO.getDateDebut());
        evenement.setTitre(evenementDTO.getTitre());

        Evenement updatedEvenement = evenementRepository.save(evenement);
        return new EvenementDTO(updatedEvenement);
    }

    /**
     * Méthode pour supprimer un événement
     * @param id l'identifiant de l'événement à supprimer
     * @throws ChangeSetPersister.NotFoundException si l'événement n'existe pas
     */
    public void deleteEvenement(Long id) throws ChangeSetPersister.NotFoundException {
        if (!evenementRepository.existsById(id)) {
            throw new ChangeSetPersister.NotFoundException();
        }
        evenementRepository.deleteById(id);
    }
}