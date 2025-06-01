package com.example.javaprojet.services;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.entity.Ressource;
import com.example.javaprojet.dto.ListDiffusionDTO;
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

    public ListDiffusionDTO create(ListDiffusionDTO liste) {
        ListeDiffusion listeDiffusion = new ListeDiffusion(liste);
        ListeDiffusion saved = listeDiffusionRepository.save(listeDiffusion);
        return new ListDiffusionDTO(saved);
    }

    @Transactional(readOnly = true)
    public Optional<ListDiffusionDTO> findById(ListDiffusionDTO dto) {
        long id = dto.getId();
        Optional<ListeDiffusion> entity = listeDiffusionRepository.findById(id);
        return entity.map(ListDiffusionDTO::new);
    }

    @Transactional(readOnly = true)
    public List<ListDiffusionDTO> findAll() {
        List<ListeDiffusion> entities = listeDiffusionRepository.findAll();
        return entities.stream()
                .map(ListDiffusionDTO::new)
                .collect(Collectors.toList());
    }

    //  UPDATE
    public ListDiffusionDTO update(ListDiffusionDTO updated) {
        long id = updated.getId();
        ListeDiffusion updatedEntity = listeDiffusionRepository.findById(id)
                .map(existing -> {
                    existing.setNom(updated.getNom());
                    existing.setType(updated.getType());
                    existing.setChemin(updated.getChemin());
                    existing.setDateCreation(updated.getDateCreation());
                    existing.setDescription(updated.getDescription());
                    existing.setEstSysteme(updated.isEstSysteme());
                    existing.setProjet(updated.getProjet());
                    return listeDiffusionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Liste de diffusion non trouvée avec id " + id));

        return new ListDiffusionDTO(updatedEntity);
    }

    //  DELETE
    public void delete(ListDiffusionDTO deleted) {
        long id = deleted.getId();
        if (!listeDiffusionRepository.existsById(id)) {
            throw new RuntimeException("Liste non trouvée avec id " + id);
        }
        listeDiffusionRepository.deleteById(id);
    }

    // Liste des abonnés
    @Transactional(readOnly = true)
    public Set<UtilisateurDTO> getAbonnes(ListDiffusionDTO listes) {
        long listeId = listes.getId();
        ListeDiffusion liste = listeDiffusionRepository.findById(listeId)
                .orElseThrow(() -> new RuntimeException("Liste non trouvée"));

        return liste.getAbonnes().stream()
                .map(UtilisateurDTO::new) // Assuming UtilisateurDTO has a constructor that takes Utilisateur
                .collect(Collectors.toSet());
    }

    @Transactional(readOnly = true)
    public List<Ressource> getRessourcesPubliques() {
        return ressourceRepository.findAll()
                .stream()
                .filter(ressource -> "public".equalsIgnoreCase(ressource.getType()))
                .collect(Collectors.toList());
    }
}