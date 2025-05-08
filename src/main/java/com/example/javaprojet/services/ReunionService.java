package com.example.javaprojet.services;


import com.example.javaprojet.entity.Reunion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.repo.ReunionRepository;
import com.example.javaprojet.repo.UtilisateurRepesitory;
import com.example.javaprojet.repo.ProjetRepesitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ReunionService {

    private final ReunionRepository reunionRepository;
    private final UtilisateurRepesitory utilisateurRepository;
    private final ProjetRepesitory projetRepository;

    @Autowired
    public ReunionService(ReunionRepository reunionRepository,
                          UtilisateurRepesitory utilisateurRepository,
                          ProjetRepesitory projetRepository) {
        this.reunionRepository = reunionRepository;
        this.utilisateurRepository = utilisateurRepository;
        this.projetRepository = projetRepository;
    }

    public Reunion createWithProjet(Reunion reunion, Long projetId) {
        Projet projet = projetRepository.findById(projetId)
                .orElseThrow(() -> new RuntimeException("Projet non trouvé avec id " + projetId));
        reunion.setProjet(projet);
        return reunionRepository.save(reunion);
    }

    public Reunion ajouterParticipant(Long reunionId, Long utilisateurId) {
        Reunion reunion = reunionRepository.findById(reunionId)
                .orElseThrow(() -> new RuntimeException("Réunion non trouvée avec id " + reunionId));

        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId)
                .orElseThrow(() -> new RuntimeException("Utilisateur non trouvé avec id " + utilisateurId));

        reunion.getParticipants().add(utilisateur);
        return reunionRepository.save(reunion);
    }

    public Reunion update(Long id, Reunion updated) {
        return reunionRepository.findById(id)
                .map(existing -> {
                    existing.setTitre(updated.getTitre());
                    existing.setDescription(updated.getDescription());
                    existing.setDate(updated.getDate());
                    existing.setLienMeet(updated.getLienMeet());
                    existing.setDuree(updated.getDuree());
                    existing.setEstObligatoire(updated.isEstObligatoire());
                    existing.setProjet(updated.getProjet());
                    existing.setParticipants(updated.getParticipants());
                    return reunionRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Réunion non trouvée avec id " + id));
    }

    @Transactional(readOnly = true)
    public Optional<Reunion> findById(Long id) {
        return reunionRepository.findById(id);
    }

    @Transactional(readOnly = true)
    public List<Reunion> findAll() {
        return reunionRepository.findAll();
    }

    public void delete(Long id) {
        if (!reunionRepository.existsById(id)) {
            throw new RuntimeException("Impossible de supprimer, réunion non trouvée avec id " + id);
        }
        reunionRepository.deleteById(id);
    }
}

