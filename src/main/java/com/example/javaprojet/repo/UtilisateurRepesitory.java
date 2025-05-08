package com.example.javaprojet.repo;

import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UtilisateurRepesitory extends JpaRepository<Utilisateur,Long> {
    List<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByEmailAndMotDePasse(String email, String motDePasse);
    //List<Utilisateur> findByIdAndIdAndProjets(Long id, Projet projet);
    Utilisateur findUtilisateurByID(long id);

    List<Utilisateur> findByNomContainingIgnoreCase(String nom);
}
