package com.example.javaprojet.services;
import com.example.javaprojet.dto.CalendrierDTO;
import com.example.javaprojet.entity.Calendrier;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.CalendrierRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class CalendrierService {

    private final CalendrierRepository calendrierRepository;

    @Autowired
    public CalendrierService(CalendrierRepository calendrierRepository) {
        this.calendrierRepository = calendrierRepository;
    }


    public Calendrier create(CalendrierDTO calendrierDTO, Utilisateur utilisateur) {
        Calendrier calendrier = new Calendrier(calendrierDTO);
        calendrier.setProprietaire(utilisateur);
        return calendrierRepository.save(calendrier);
    }

    public Calendrier update(Long id, Calendrier updated) {
        return calendrierRepository.findById(id)
                .map(existing -> {
                    existing.setNom(updated.getNom());
                    existing.setEstPartage(updated.isEstPartage());
                    existing.setProprietaire(updated.getProprietaire());
                    existing.setEvenements(updated.getEvenements());
                    existing.setUtilisateursPartages(updated.getUtilisateursPartages());
                    return calendrierRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Calendrier non trouvé avec id " + id));
    }

    // TODO : Has to rethink this design later
    public Calendrier update(CalendrierDTO updatedDTO) {
        return calendrierRepository.findById(updatedDTO.getId())
                .map(existing -> {
                    existing.setNom(updatedDTO.getNom());
                    existing.setEstPartage(updatedDTO.isEstPartage());
                    return calendrierRepository.save(existing);
                })
                .orElseThrow(null);
    }


    @Transactional(readOnly = true)
    public Calendrier findById(Long id) {
        return calendrierRepository.findById(id).orElse(null);
    }

    @Transactional(readOnly = true)
    public List<Calendrier> findAll() {
        return calendrierRepository.findAll();
    }


    public void delete(Long id) {
        calendrierRepository.deleteById(id);
    }
}

