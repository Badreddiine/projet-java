package com.example.javaprojet.services;
import com.example.javaprojet.entity.Calendrier;
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


    public Calendrier create(Calendrier calendrier) {
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


    @Transactional(readOnly = true)
    public Optional<Calendrier> findById(Long id) {
        return calendrierRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<Calendrier> findAll() {
        return calendrierRepository.findAll();
    }


    public void delete(Long id) {
        if (!calendrierRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer, calendrier non trouvé avec id " + id);
        }
        calendrierRepository.deleteById(id);
    }
}

