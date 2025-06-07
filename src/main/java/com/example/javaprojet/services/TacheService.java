package com.example.javaprojet.services;

import com.example.javaprojet.dto.TacheDTO;
import com.example.javaprojet.entity.Tache;
import com.example.javaprojet.entity.Utilisateur;

import com.example.javaprojet.repo.TacheRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TacheService {
    @Autowired
    TacheRepository tacheRepesitory;

    /**
     *
     * @param tache il faut recuperer de dto dans controlleur
     * @return une entite
     */
    @Transactional
    public Tache creerTache(Tache tache) {

        return tacheRepesitory.save(tache);
    }

    /**
     * une methode pour modifier une tache
     * @param tacheModifiee un dto ppour envoyer les donnes modifier
     * @return entitee modifier
     */
    @Transactional
    public Tache mettreAJourTache( TacheDTO tacheModifiee) {
        long id = tacheModifiee.getId();
        Optional<Tache> tacheExistante = tacheRepesitory.findById(id);
        if (tacheExistante.isPresent()) {
            Tache tache = tacheExistante.get();
            tache.setTitre(tacheModifiee.getTitre());
            tache.setDescription(tacheModifiee.getDescription());
            tache.setDateDebut(tacheModifiee.getDateDebut());
            tache.setDateFin(tacheModifiee.getDateFin());
            tache.setPriorite(tacheModifiee.getPriorite());
            tache.setDifficulte(tacheModifiee.getDifficulte());
            tache.setEtat(tacheModifiee.getEtat());
            tache.setNotation(tacheModifiee.getNotation());
            tache.setAssigneA(tacheModifiee.getAssigneA());
            return tacheRepesitory.save(tache);
        } else {
            throw new RuntimeException("Tâche non trouvée avec l'ID : " + id);
        }
    }

    /**
     * methode pour supremer tache
     * @param id de tache a sup
     */
    @Transactional
    public void supprimerTache(Long id) {
        tacheRepesitory.deleteById(id);
    }

    /**
     * recuperer tache par sont id
     * @param id
     * @return
     */
    public Tache recupererTacheParId(Long id) {
        Optional<Tache> tache = tacheRepesitory.findById(id);
        return tache.orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
    }

    /**
     * recuperer tout les tache
     * @return
     */
    public List<Tache> recupererToutesLesTaches() {
        return tacheRepesitory.findAll();
    }

    /**
     * recuperer tache par etas
     * @param etat ce sont les atrubus de enum etas
     * @return lest des taches
     */
    public List<Tache> recupererTachesParEtat(String etat) {
        return tacheRepesitory.findByEtat(etat);
    }

    /**
     * methode pour recuperer tout les tache d un etutilisateur
     * @param utilisateurId
     * @return
     */
    public List<Tache> recupererTachesParUtilisateur(Long utilisateurId) {
        return tacheRepesitory.findByAssigneA_Id(utilisateurId);
    }

    public List<Tache> recupererTachesParProjet(Long projetId) {
        return tacheRepesitory.findByProjet_Id(projetId);
    }

    /**
     * chenger  etas de tache
     * @param idTche  a recuprer de dto soit dans front end soit  ou niveau de back end
     * @param nouvelEtat se sont les etas de enums
     * @return tache avec etas changer
     */
    @Transactional
    public Tache changerEtat(Long idTche, String nouvelEtat) {
        Optional<Tache> tacheExistante = tacheRepesitory.findById(idTche);
        if (tacheExistante.isPresent()) {
            Tache tache = tacheExistante.get();
            tache.setEtat(nouvelEtat);
            return tacheRepesitory.save(tache);
        } else {
            throw new RuntimeException("Tâche non trouvée avec l'ID : " + idTche);
        }
    }

    /**
     * prend une tache  une tache a un user
     * @param id  a recuperer dans front end
     * @param utilisateur a recuperer de courent user
     * @return tache assigner a user
     */
    @Transactional
    public Tache prendUneTache(Long id, Utilisateur utilisateur) {
        Optional<Tache> tacheExistante = tacheRepesitory.findById(id);
        if (tacheExistante.isPresent()) {
            Tache tache = tacheExistante.get();
            tache.setAssigneA(utilisateur);
            return tacheRepesitory.save(tache);
        } else {
            throw new RuntimeException("Tâche non trouvée avec l'ID : " + id);
        }
    }

}
