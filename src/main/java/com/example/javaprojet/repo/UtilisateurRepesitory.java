package com.example.javaprojet.repo;

import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface UtilisateurRepesitory extends JpaRepository<Utilisateur,Long> {
    List<Utilisateur> findByEmail(String email);
    List<Utilisateur> findByEmailAndMotDePasse(String email, String motDePasse);
    //List<Utilisateur> findByIdAndIdAndProjets(Long id, Projet projet);
    Utilisateur findUtilisateurById(long id);
    List<Utilisateur> findByNomContainingIgnoreCase(String nom);

    Optional<Utilisateur> getDistinctByEmail(String email);

    @Query("SELECT COUNT(c) > 0 FROM Calendrier c WHERE c.id=:calendarId AND c.proprietaire.id=:utilisateurId")
    boolean hasCalendar(Long utilisateurId, Long calendarId);
}
