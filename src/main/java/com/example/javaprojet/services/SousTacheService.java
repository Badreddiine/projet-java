package com.example.javaprojet.services;

import com.example.javaprojet.dto.SousTacheDTO;
import com.example.javaprojet.entity.SousTache;
import com.example.javaprojet.repo.SousTacheRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
@RequiredArgsConstructor
public class SousTacheService {


    private SousTacheRepository sousTacheRepository;

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
            sousTache.setDateDebut(sousTacheModifiee.getDateDebut());
            sousTache.setDateFin(sousTacheModifiee.getDateFin());
            sousTache.setEtat(sousTacheModifiee.getEtat());
            return sousTacheRepository.save(sousTache);
        } else {
            throw new RuntimeException("Sous-tâche non trouvée avec l'ID : " + id);
        }
    }

    /**
     * suorimer une soustache
     * @param id recuperer dans lle cintroleur a partir de dto
     */
    public void supprimerSousTache(Long id) {
        sousTacheRepository.deleteById(id);
    }
    /**
     * reccuprer   une soustache  par sons id
     * @param id recuperer dans lle cintroleur a partir de dto
     */
    public SousTache recupererSousTacheParId(Long id) {
        Optional<SousTache> sousTache = sousTacheRepository.findById(id);
        return sousTache.orElseThrow(() -> new RuntimeException("Sous-tâche non trouvée avec l'ID : " + id));
    }

    public List<SousTache> recupererToutesLesSousTaches() {
        return sousTacheRepository.findAll();
    }


    /**
     * recuperer sous tache par sons etas por admine de projet
     * @param etat
     * @return
     */
    public List<SousTache> recupererSousTachesParEtat(String etat) {
        return sousTacheRepository.findByEtat(etat);
    }

    /**
     * methode pour changer etat par user
     * @param id recuperer apartire de dto dans controlleur
     * @param nouvelEtat chaien de cararcter qui sera comme trois champs dans front end
     * @return
     */
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
}