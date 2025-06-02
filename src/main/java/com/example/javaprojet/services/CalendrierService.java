package com.example.javaprojet.services;

import com.example.javaprojet.dto.CalendrierDTO;
import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.repo.CalendrierRepository;
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
public class CalendrierService {
    private final CalendrierRepository calendrierRepository;

    /**
     * Méthode pour créer un calendrier
     * @param calendrierDTO le calendrier qui vient du front-end
     * @return CalendrierDTO l'objet calendrier créé converti en DTO
     */
    public CalendrierDTO createCalendrier(CalendrierDTO calendrierDTO) {
        Calendrier calendrier = new Calendrier(calendrierDTO);
        Calendrier savedCalendrier = calendrierRepository.save(calendrier);
        return new CalendrierDTO(savedCalendrier);
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
     * @param id l'identifiant du calendrier à modifier
     * @param calendrierDTO les données modifiées du front-end sous format DTO
     * @return CalendrierDTO le calendrier mis à jour
     * @throws ChangeSetPersister.NotFoundException si le calendrier n'existe pas
     */
    public CalendrierDTO updateCalendrier(Long id, CalendrierDTO calendrierDTO) throws ChangeSetPersister.NotFoundException {
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
