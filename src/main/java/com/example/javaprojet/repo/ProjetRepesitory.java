package com.example.javaprojet.repo;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProjetRepesitory extends JpaRepository<Projet, Long> {
     List<Projet> findByEstPublic(boolean estPublic);
     @Query("SELECT g.membres FROM Projet p JOIN p.groupe g WHERE p.id = :projetId AND :userId MEMBER OF p.membres")
     List<Utilisateur> findMembresIfUserHasAccess(@Param("projetId") Long projetId,
                                                  @Param("userId") Long userId);
     @Modifying
     @Query("UPDATE Projet p SET p.membres = (SELECT m FROM p.membres m WHERE m.id <> :utilisateurId)")
     void retirerUtilisateurDeTousLesProjets(@Param("utilisateurId") Long utilisateurId);
}

