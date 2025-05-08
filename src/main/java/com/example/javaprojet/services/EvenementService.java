package com.example.javaprojet.services;

import com.example.javaprojet.entity.Evenement;
import com.example.javaprojet.repo.EvenementRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class EvenementService {

    private final EvenementRepository evenementRepository;

    @Autowired
    public EvenementService(EvenementRepository evenementRepository) {
        this.evenementRepository = evenementRepository;
    }


    public Evenement create(Evenement evenement) {
        return evenementRepository.save(evenement);
    }


    public Evenement update(Long id, Evenement updated) {
        return evenementRepository.findById(id)
                .map(existing -> {
                    existing.setTitre(updated.getTitre());
                    existing.setDescription(updated.getDescription());
                    existing.setDateDebut(updated.getDateDebut());
                    existing.setDateFin(updated.getDateFin());
                    existing.setLieu(updated.getLieu());
                    existing.setEstRecurrent(updated.isEstRecurrent());
                    existing.setCalendrier(updated.getCalendrier());
                    existing.setParticipants(updated.getParticipants());
                    return evenementRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Événement non trouvé avec id " + id));
    }


    @Transactional(readOnly = true)
    public Optional<Evenement> findById(Long id) {
        return evenementRepository.findById(id);
    }


    @Transactional(readOnly = true)
    public List<Evenement> findAll() {
        return evenementRepository.findAll();
    }


    public void delete(Long id) {
        if (!evenementRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer, événement non trouvé avec id " + id);
        }
        evenementRepository.deleteById(id);
    }
}
