package com.example.javaprojet.services;

import com.example.javaprojet.dto.ProjetDTO;
import com.example.javaprojet.dto.TacheDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Tache;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.TacheRepesitory;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Service
public class TacheService {
    @Autowired
    TacheRepesitory tacheRepesitory;
    @Transactional
    public Tache creerTache(TacheDTO tache) {
        Tache newTache = new Tache(tache);

        return tacheRepesitory.save(newTache);
    }

    @Transactional
    public Tache mettreAJourTache( TacheDTO tacheModifiee) {
        Tache newTache = new Tache(tacheModifiee);
        Long id=newTache.getId();
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

            return tacheRepesitory.save(tache);
        } else {
            throw new RuntimeException("Tâche non trouvée avec l'ID : " + id);
        }
    }

    @Transactional
    public void supprimerTache(TacheDTO tacheDTO) {
        long id=tacheDTO.getId();

        tacheRepesitory.deleteById(id);
    }
@Transactional
    public Tache recupererTacheParId(TacheDTO tacheDTO) {
        long id=tacheDTO.getId();
        Optional<Tache> tache = tacheRepesitory.findById(id);
        return tache.orElseThrow(() -> new RuntimeException("Tâche non trouvée avec l'ID : " + id));
    }

    public List<TacheDTO> recupererToutesLesTaches() {
        return tacheRepesitory.findAll()
                .stream()
                .map(TacheDTO::new)  // Si TacheDTO a un constructeur qui prend une Tache
                .collect(Collectors.toList());
    }

    public List<TacheDTO> recupererTachesParEtat(String etat) {
        return tacheRepesitory.findByEtat(etat)  // Correction: tacheRepository
                .stream()
                .map(TacheDTO::new)  // Si TacheDTO a un constructeur qui prend une Tache
                .collect(Collectors.toList());
    }
//    public List<TacheDTO> recupererTachesParEtat(String etat) {
//        ;
//
//        return tacheRepesitory.findByEtat(stream()
//                                                etat);
//    }
@Transactional
    public List<TacheDTO> recupererTachesParUtilisateur(UtilisateurDTO utilisateur) {
        long id=utilisateur.getId();

//        return tacheRepesitory.findByAssigneA_Id(id);
        return tacheRepesitory.findByAssigneA_Id(id)  // Correction: tacheRepository
                .stream()
                .map(TacheDTO::new)  // Si TacheDTO a un constructeur qui prend une Tache
                .collect(Collectors.toList());
    }


    @Transactional
    public Tache changerEtat(TacheDTO tachedto, String nouvelEtat) {
        long id = tachedto.getId();
        Optional<Tache> tacheExistante = tacheRepesitory.findById(id);

        if (tacheExistante.isPresent()) {
            Tache tache = tacheExistante.get();
            tache.setEtat(nouvelEtat);
            return tacheRepesitory.save(tache);
        } else {
            throw new RuntimeException("Tâche non trouvée avec l'ID : " + id);
        }
    }
    @Transactional
    public Tache assignerA(TacheDTO tachedto , UtilisateurDTO utilisateurDTO) {
        Utilisateur utilis=new Utilisateur(utilisateurDTO);
        long id = tachedto.getId();
        Optional<Tache> tacheExistante = tacheRepesitory.findById(id);
        if (tacheExistante.isPresent()) {
            Tache tache = tacheExistante.get();
            tache.setAssigneA(utilis);
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
