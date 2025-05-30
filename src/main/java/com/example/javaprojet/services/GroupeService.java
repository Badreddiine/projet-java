package com.example.javaprojet.services;

import com.example.javaprojet.entity.Groupe;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.RoleType;
import com.example.javaprojet.repo.GroupeRepesitory;
import com.example.javaprojet.repo.UtilisateurRepesitory;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author $ {USERS}
 **/
@Service
public class GroupeService {
    @Autowired
    GroupeRepesitory groupeRepesitory;
    @Autowired
    UtilisateurRepesitory utilisateurRepesitory;
    public void rejoindreGroupe(Long idGroupe,Long idUserConnecter) {
        Utilisateur utilisateur=utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException("User non trouvé"));
        Groupe groupe=groupeRepesitory.findById(idGroupe)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé"));
        if(utilisateur != null && groupe != null) {
            groupe.getMembres().add(utilisateur);
        }
    }

    public void creationGroupe(Groupe groupe ,Long idUserConnecter) {
        Utilisateur utilisateur=utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException("user non trouvé"));
        if(groupe.getMembres().size()>0) {
            throw new IllegalStateException("Groupe déjà existant");
        }
        if(utilisateur != null && groupe != null &&utilisateur.getRole()!= RoleType.ADMIN) {
            groupeRepesitory.save(groupe);
        }
    }
    public void supprimerGroupe(Long idGroupe, Long idUserConnecter) {
        Utilisateur utilisateur=utilisateurRepesitory.findById(idUserConnecter)
                .orElseThrow(() -> new EntityNotFoundException("user non trouvé"));
        Groupe groupe =groupeRepesitory.findById(idGroupe)
                .orElseThrow(() -> new EntityNotFoundException("Groupe non trouvé"));
        if(utilisateur != null && groupe != null &&utilisateur.getRole()!= RoleType.ADMIN) {
            groupeRepesitory.deleteById(idGroupe);
        }
    }

    public List<Groupe> getAllGroupe() {
        return groupeRepesitory.findAll();
    }
}
