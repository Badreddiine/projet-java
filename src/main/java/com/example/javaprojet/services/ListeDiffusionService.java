package com.example.javaprojet.services;
import com.example.javaprojet.dto.EvenementDTO;
import com.example.javaprojet.dto.ListDiffusionDTO;
import com.example.javaprojet.entity.ListeDiffusion;
import com.example.javaprojet.entity.Ressource;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.ListeDiffusionRepository;
import com.example.javaprojet.repo.RessourceRepository;
import com.example.javaprojet.repo.UtilisateurRepository;
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
    private final UtilisateurRepository utilisateurRepository;
    private final RessourceRepository ressourceRepository;
    private  ProjetService projetService;


    public ListeDiffusionService(ListeDiffusionRepository listeDiffusionRepository,
                                 UtilisateurRepository utilisateurRepository,
                                 RessourceRepository ressourceRepository) {
        this.listeDiffusionRepository = listeDiffusionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.ressourceRepository = ressourceRepository;
    }

    /**
     * methode pour cree une liste de diffusion
     * @param liste il faut le reccuperer de puis un dto dans le controlleur
     * @return
     */
    public ListeDiffusion create(ListeDiffusion liste) {

        return listeDiffusionRepository.save(liste);
    }

    /**
     * recuperer les liste de diffusion par id
     * @param id  il faut reccuperer du dto
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<ListDiffusionDTO> findById(Long id) {

//        return listeDiffusionRepository.findById(id);
        return listeDiffusionRepository.findById(id)
                .map(ListDiffusionDTO::new);
    }

    /**
     * methode pour afficher tout les listes de diffusion
     * @return
     */
    @Transactional(readOnly = true)
    public List<ListDiffusionDTO> findAll() {

        List<ListeDiffusion> l= listeDiffusionRepository.findAll();
         return l.stream()
                 .map(ListDiffusionDTO::new)
                 .collect(Collectors.toList());
    }

    /**
     * methode pour faire mise a jour des listes de diffusion

     * @param updated les donnes a recuperer
     * @return
     */
    public ListDiffusionDTO update( ListDiffusionDTO updated) {
        Long id =updated.getId();
        return listeDiffusionRepository.findById(id)
                .map(existing -> {
                    existing.setNom(updated.getNom());
                    existing.setType(updated.getType());
                    existing.setChemin(updated.getChemin());
                    existing.setDateCreation(updated.getDateCreation());
                    existing.setDescription(updated.getDescription());
                    existing.setEstSysteme(updated.isEstSysteme());
                    existing.setProjet(projetService.findProjetById(updated.getProjetId()));
                  ListeDiffusion listeDiffusion =listeDiffusionRepository.save(existing);
                  return new ListDiffusionDTO(listeDiffusion);
                })
                .orElseThrow(() -> new RuntimeException("Liste de diffusion non trouvée avec id " + id));
    }

    /**
     * methode pour supprimer une liste de diffusion
     * @param id le id il faut le reccupere dans le controlleur du dto
     */
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

