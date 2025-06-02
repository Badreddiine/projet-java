package com.example.javaprojet.services;
import com.example.javaprojet.dto.CalendrierDTO;
import com.example.javaprojet.dto.EvenementDTO;
import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.entity.Evenement;
import com.example.javaprojet.repo.EvenementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class EvenementService {
    private final EvenementRepository evenementRepository;

    /**
     * Méthode pour créer un événement en ajoutant un calendrier
     * @param evenementDTO les données de l'événement
     * @param calendrierDTO les données du calendrier
     * @return EvenementDTO l'événement créé converti en DTO
     */
    public EvenementDTO createEvenement(EvenementDTO evenementDTO, CalendrierDTO calendrierDTO) {
        Calendrier calendrier = new Calendrier(calendrierDTO);
        Evenement evenement = new Evenement(evenementDTO);
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
     * Méthode pour modifier les données d'un événement
     * @param id l'identifiant de l'événement courant
     * @param evenementDTO le DTO envoyé depuis le front-end
     * @return EvenementDTO l'entité modifiée convertie en DTO
     * @throws ChangeSetPersister.NotFoundException si l'événement n'existe pas
     */
    public EvenementDTO updateEvenement(Long id, EvenementDTO evenementDTO) throws ChangeSetPersister.NotFoundException {
        Evenement evenement = evenementRepository.findById(id)
                .orElseThrow(ChangeSetPersister.NotFoundException::new);

        // Mise à jour des propriétés (le calendrier reste inchangé)
        evenement.setLieu(evenementDTO.getLieu());
        evenement.setDescription(evenementDTO.getDescription());
        evenement.setDateFin(evenementDTO.getDateFin());
        evenement.setDateDebut(evenementDTO.getDateDebut());
        evenement.setTitre(evenementDTO.getTitre()); // Ajout du titre si présent

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