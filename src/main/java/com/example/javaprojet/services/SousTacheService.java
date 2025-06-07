package com.example.javaprojet.services;

import com.example.javaprojet.entity.SousTache;
import com.example.javaprojet.entity.Tache;
import com.example.javaprojet.repo.SousTacheRepesitory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class SousTacheService {

    @Autowired
    private SousTacheRepesitory sousTacheRepository;

    /**
     * methode pour cree une sous tache
     * @param sousTache ilfaut le reccuperer de dto dans le controlleur
     * @return
     */
    public SousTache creerSousTache(SousTache sousTache) {

        return sousTacheRepository.save(sousTache);
    }

    /**
     * methode pour mettre ajour les dous tache
     * @param id il faut le recuperer soit en front end soit ou niveau de controlleur apartire de dto
     * @param sousTacheModifiee dto qui contient les donnes modifier
     * @return entitee modifier
     */
    public SousTache mettreAJourSousTache(Long id, SousTacheDTO sousTacheModifiee) {
        Optional<SousTache> sousTacheExistante = sousTacheRepository.findById(id);
        if (sousTacheExistante.isPresent()) {
            SousTache sousTache = sousTacheExistante.get();
            sousTache.setTitre(sousTacheModifiee.getTitre());
            sousTache.setDescription(sousTacheModifiee.getDescription());
            sousTache.setTags(sousTacheModifiee.getTags());
            sousTache.setDateDebut(sousTacheModifiee.getDateDebut());
            sousTache.setDateFin(sousTacheModifiee.getDateFin());
            sousTache.setEtat(sousTacheModifiee.getEtat());
            sousTache.setEstTerminee(sousTacheModifiee.isEstTerminee());
            return sousTacheRepository.save(sousTache);
        } else {
            throw new RuntimeException("Sous-tâche non trouvée avec l'ID : " + id);
        }
    }

    /**
     * 
     * @param id
     */
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
            sousTache.setEstTerminee(estTerminee);
            return sousTacheRepository.save(sousTache);
        } else {
            throw new RuntimeException("Sous-tâche non trouvée avec l'ID : " + id);
        }
    }
}