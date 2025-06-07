package com.example.javaprojet.repo;
import com.example.javaprojet.entity.Projet;

import com.example.javaprojet.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjetRepesitory extends JpaRepository<Projet, Long> {
     List<Projet> findByEstPublic(boolean estPublic);
     List<Projet> findByAdmin(Utilisateur admin);

     @Query("SELECT m FROM Projet p JOIN p.membres m WHERE p.id = :projetId AND " +
             "(p.estPublic = true OR EXISTS (SELECT u FROM p.membres u WHERE u.id = :utilisateurId))")
     List<Utilisateur> findMembresIfUserHasAccess(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

     @Query("SELECT p FROM Projet p WHERE p.estPublic = true OR " +
             "EXISTS (SELECT u FROM p.membres u WHERE u.id = :utilisateurId)")
     List<Projet> findProjetsAccessiblesByUser(@Param("utilisateurId") Long utilisateurId);
}


