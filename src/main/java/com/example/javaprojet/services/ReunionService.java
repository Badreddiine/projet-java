package com.example.javaprojet.services;


import com.example.javaprojet.dto.ReunionDTO;
import com.example.javaprojet.entity.Reunion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.repo.ReunionRepository;
import com.example.javaprojet.repo.ProjetRepesitory;
import com.example.javaprojet.repo.UtilisateurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReunionService {

    private final ReunionRepository reunionRepository;
    private final UtilisateurRepository utilisateurRepository;
    private final ProjetRepesitory projetRepository;

    @Autowired
    public ReunionService(ReunionRepository reunionRepository,
                          UtilisateurRepository utilisateurRepository,
                          ProjetRepesitory projetRepository) {
        this.reunionRepository = reunionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.projetRepository = projetRepository;
    }

    /**
     * methode pour cree uen creat reunion avec projet
     * @param reunion il faur lle recuperer apertire de dto dans la couche cntroller a l aide de dto (constructeur)
     * @param projetId  il faur lle recuperer apertire de dto dans la couche cntroller a l aide de dto
     * @return
     */
    public Reunion createWithProjet(Reunion reunion, Long projetId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec id " + projetId));
        reunion.setProjet(projet);
        Reunion reunion1=reunionRepository.save(reunion);
       reunion1.getProjet().getMembres().size();
       return reunion1;
    }

    /**
     * methode pour ajouter un participant a une reunion
     * @param reunionId il faut le recuperer de dto dans le controlleuur
     * @param utilisateurId il faut le recuperer de dto dans le controlleuur
     * @return
     */
    public Reunion ajouterParticipant(Long reunionId, Long utilisateurId) {
        Reunion reunion = reunionRepository.findById(reunionId)
                .orElseThrow(() -> new RuntimeException("Réunion non trouvée avec id " + reunionId));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec id " + utilisateurId));

        reunion.getParticipants().add(utilisateur);
        return reunionRepository.save(reunion);
    }

    /**
     * methode pour faire misse a jour de reunion
     * @param reunionDTO on  vas le reccuperer de dans controlleur
     * @return
     */
    public Reunion update( ReunionDTO reunionDTO) {
        long id = reunionDTO.getId();
        return reunionRepository.findById(id)
                .map(existing -> {
                    existing.setTitre(reunionDTO.getTitre());
                    existing.setDescription(reunionDTO.getDescription());
                    existing.setDate(reunionDTO.getDate());
                    existing.setLienMeet(reunionDTO.getLienMeet());
                    existing.setDuree(reunionDTO.getDuree());
                    existing.setEstObligatoire(reunionDTO.isEstObligatoire());
                    return reunionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Réunion non trouvée avec id " + id));
    }

    /**
     * methode pour recuperer une renion par sons id
     * @param id il afaut le recuperer de puit le dto dans le controlleur
     * @return
     */
    @Transactional(readOnly = true)
    public Optional<Reunion> findById(Long id) {
        return reunionRepository.findById(id);
    }

    /**
     * methode pour afficher tout la liste de tout les reunion
     * @return
     */
    @Transactional(readOnly = true)
    public List<Reunion> findAll() {
        return reunionRepository.findAll();
    }

    /**
     * ethode pour suprimer un reunion
     * @param id le id il faut le recuperer de puit sons dto dans le controlleur
     */
    public void delete(Long id) {
        if (!reunionRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer, réunion non trouvée avec id " + id);
        }
        reunionRepository.deleteById(id);
    }
}

