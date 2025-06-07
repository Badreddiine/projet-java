package com.example.javaprojet.services;

import com.example.javaprojet.dto.GroupeDTO;
import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.RoleType;
import com.example.javaprojet.repo.GroupeRepository;
import com.example.javaprojet.repo.UtilisateurRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * @author $ {USERS}
 **/
@Service
@Transactional
@RequiredArgsConstructor
public class GroupeService {

    private final GroupeRepository groupeRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     * methode pour ajouter dans un group
     *
     * @param idGroupe recuperer dans controlleur de dto
     * @param idUserConnecter recuperer de controlleur de dto de utilisateur
     *
     */
    public void rejoindreGroupe(Long idGroupe, Long idUserConnecter) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException("User non trouvé"));
        Groupe groupe = groupeRepository.findById(idGroupe)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé"));

        if (utilisateur != null && groupe != null) {
            groupe.getMembres().add(utilisateur);
            groupeRepository.save(groupe);
        }
    }

    /**
     * methode pour creation de group
     * @param groupe il faut recuprer de gto a laide de constructeur
     * @param idUserConnecter recuperer de dto de utilisateur
     */
    public void creationGroupe(Groupe groupe, Long idUserConnecter) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException("user non trouvé"));

        if (groupe.getMembres().size() > 0) {
            throw new IllegalStateException("Groupe déjà existant");
        }

        if (utilisateur != null && groupe != null && utilisateur.getRole() != RoleType.ADMIN) {
            groupeRepository.save(groupe);
        }
    }

    /**
     * methode suprimmer un groupe
     * @param idGroupe  recuprer de dto dans controlleur
     * @param idUserConnecter recuprer de dto dans controlleur
     */
    public void supprimerGroupe(Long idGroupe, Long idUserConnecter) {
        Utilisateur utilisateur = utilisateurRepository.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException("user non trouvé"));
        Groupe groupe = groupeRepository.findById(idGroupe)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé"));

        if (utilisateur != null && groupe != null && utilisateur.getRole() != RoleType.ADMIN) {
            groupeRepository.deleteById(idGroupe);
        }
    }

    /**
     * Obtenir un groupe par ID - version qui lance une exception si non trouvé
     * Pour compatibilité avec le controller
     */
    public Groupe findGroupeById(Long id) {
        return groupeRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé avec l'ID: " + id));
    }

    /**
     * Obtenir un groupe par ID - version qui retourne Optional
     */
    public Optional<Groupe> getGroupeById(Long id) {

        return groupeRepository.findById(id);
    }

    /**
     * Sauvegarder un groupe
     */
    public Groupe saveGroupe(Groupe groupe) {
        return groupeRepository.save(groupe);
    }

    /**
     * Obtenir tous les groupes
     */
    public List<Groupe> getAllGroupes() {
        return groupeRepository.findAll();
    }
    public Groupe getById(Long id) {
        Groupe groupe = groupeRepository.getGroupeById(id);
        if (groupe == null) {
            throw new EntityNotFoundException("Groupe not found with id: " + id);
        }
        return groupe;
    }

}