package com.example.javaprojet.services;

import com.example.javaprojet.entity.SousTache;
import com.example.javaprojet.repo.SousTacheRepesitory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SousTacheService {

    @Autowired
    private SousTacheRepesitory sousTacheRepository;

    @Transactional
    public SousTache creerSousTache(SousTache sousTache) {
        return sousTacheRepository.save(sousTache);
    }

    @Transactional
    public SousTache mettreAJourSousTache(Long id, SousTache sousTacheModifiee) {
        Optional<SousTache> sousTacheExistante = sousTacheRepository.findById(id);
        if (sousTacheExistante.isPresent()) {
            SousTache sousTache = sousTacheExistante.get();
            sousTache.setTitre(sousTacheModifiee.getTitre());
            sousTache.setDescription(sousTacheModifiee.getDescription());
            sousTache.setTags(sousTacheModifiee.getTags());
            sousTache.setDateDebut(sousTacheModifiee.getDateDebut());
            sousTache.setDateFin(sousTacheModifiee.getDateFin());
            sousTache.setEtat(sousTacheModifiee.getEtat());
            sousTache.setTerminee(sousTacheModifiee.isTerminee());
            return sousTacheRepository.save(sousTache);
        } else {
            throw new RuntimeException("Sous-tâche non trouvée avec l'ID : " + id);
        }
    }

    @Transactional
    public void supprimerSousTache(Long id) {
        sousTacheRepository.deleteById(id);
    }

    public SousTache recupererSousTacheParId(Long id) {
        Optional<SousTache> sousTache = sousTacheRepository.findById(id);
        return sousTache.orElseThrow(() -> new RuntimeException("Sous-tâche non trouvée avec l'ID : " + id));
    }

    public List<SousTache> recupererToutesLesSousTaches() {
        return sousTacheRepository.findAll();
    }

    public List<SousTache> recupererSousTachesParTache(Long tacheId) {
        return sousTacheRepository.findByTache_Id(tacheId);
    }

    public List<SousTache> recupererSousTachesParEtat(String etat) {
        return sousTacheRepository.findByEtat(etat);
    }

    @Transactional
    public SousTache changerEtatSousTache(Long id, String nouvelEtat) {
        Optional<SousTache> sousTacheExistante = sousTacheRepository.findById(id);
        if (sousTacheExistante.isPresent()) {
            SousTache sousTache = sousTacheExistante.get();
            sousTache.setEtat(nouvelEtat);
            return sousTacheRepository.save(sousTache);
        } else {
            throw new RuntimeException("Sous-tâche non trouvée avec l'ID : " + id);
        }
    }

    @Transactional
    public SousTache marquerCommeTerminee(Long id, boolean estTerminee) {
        Optional<SousTache> sousTacheExistante = sousTacheRepository.findById(id);
        if (sousTacheExistante.isPresent()) {
            SousTache sousTache = sousTacheExistante.get();
            sousTache.setTerminee(estTerminee);
            return sousTacheRepository.save(sousTache);
        } else {
            throw new RuntimeException("Sous-tâche non trouvée avec l'ID : " + id);
        }
    }

    public List<SousTache> getSousTaches() {
        return sousTacheRepository.findAll();
    }
}