package com.example.javaprojet.services;

import com.example.javaprojet.entity.Tache;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.TacheRepesitory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TacheService {
    @Autowired
    TacheRepesitory tacheRepesitory;
    @Transactional
    public Tache creerTache(Tache tache) {
        return tacheRepesitory.save(tache);
    }

    @Transactional
    public Tache mettreAJourTache(Long id, Tache tacheModifiee) {
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

    @Transactional
    public void supprimerTache(Long id) {
        tacheRepesitory.deleteById(id);
    }

    public Tache recupererTacheParId(Long id) {
        Optional<Tache> tache = tacheRepesitory.findById(id);
        return tache.orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
    }

    public List<Tache> recupererToutesLesTaches() {
        return tacheRepesitory.findAll();
    }
    public List<Tache> recupererTachesParEtat(String etat) {
        return tacheRepesitory.findByEtat(etat);
    }

    public List<Tache> recupererTachesParUtilisateur(Long utilisateurId) {
        return tacheRepesitory.findByAssigneA_Id(utilisateurId);
    }

    public List<Tache> recupererTachesParProjet(Long projetId) {
        return tacheRepesitory.findByProjet_Id(projetId);
    }

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
    @Transactional
    public Tache assignerA(Long id, Utilisateur utilisateur) {
        Optional<Tache> tacheExistante = tacheRepesitory.findById(id);
        if (tacheExistante.isPresent()) {
            Tache tache = tacheExistante.get();
            tache.setAssigneA(utilisateur);
            return tacheRepesitory.save(tache);
        } else {
            throw new RuntimeException("Tâche non trouvée avec l'ID : " + id);
        }
    }

    public List<Tache> getTach(){
        List<Tache> taches = tacheRepesitory.findAll();
        return taches;
    }

}
