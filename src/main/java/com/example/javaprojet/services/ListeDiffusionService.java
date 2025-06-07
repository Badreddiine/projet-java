package com.example.javaprojet.services;
import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.entity.Ressource;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.ListeDiffusionRepository;
import com.example.javaprojet.repo.RessourceRepository;
import com.example.javaprojet.repo.UtilisateurRepesitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class ListeDiffusionService {

    private final ListeDiffusionRepository listeDiffusionRepository;
    private final UtilisateurRepesitory utilisateurRepository;
    private final RessourceRepository ressourceRepository;

    @Autowired
    public ListeDiffusionService(ListeDiffusionRepository listeDiffusionRepository,
                                 UtilisateurRepesitory utilisateurRepository,
                                 RessourceRepository ressourceRepository) {
        this.listeDiffusionRepository = listeDiffusionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ressourceRepository = ressourceRepository;
    }
    public ListeDiffusion create(ListeDiffusion liste) {
        return listeDiffusionRepository.save(liste);
    }

    @Transactional(readOnly = true)
    public Optional<ListeDiffusion> findById(Long id) {
        return listeDiffusionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<ListeDiffusion> findAll() {
        return listeDiffusionRepository.findAll();
    }

    //  UPDATE
    public ListeDiffusion update(Long id, ListeDiffusion updated) {
        return listeDiffusionRepository.findById(id)
                .map(existing -> {
                    existing.setNom(updated.getNom());
                    existing.setType(updated.getType());
                    existing.setChemin(updated.getChemin());
                    existing.setDateCreation(updated.getDateCreation());
                    existing.setDescription(updated.getDescription());
                    existing.setEstSysteme(updated.isEstSysteme());
                    existing.setProjet(updated.getProjet());
                    existing.setAbonnes(updated.getAbonnes());
                    return listeDiffusionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Liste de diffusion non trouvée avec id " + id));
    }

    //  DELETE
    public void delete(Long id) {
        if (!listeDiffusionRepository.existsById(id)) {
            throw new RuntimeException("Liste non trouvée avec id " + id);
        }
        listeDiffusionRepository.deleteById(id);
    }

    // Liste des abonnés
    @Transactional(readOnly = true)
    public Set<Utilisateur> getAbonnes(Long listeId) {
        ListeDiffusion liste = listeDiffusionRepository.findById(listeId)
                .orElseThrow(() -> new RuntimeException("Liste non trouvée"));
        return liste.getAbonnes();
    }

    @Transactional(readOnly = true)
    public List<Ressource> getRessourcesPubliques() {
        return ressourceRepository.findAll()
                .stream()
                .filter(ressource -> "public".equalsIgnoreCase(ressource.getType()))
                .collect(Collectors.toList());
    }
}

