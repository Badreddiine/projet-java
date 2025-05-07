package com.example.javaprojet.repo;

import com.example.javaprojet.entity.Groupe;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 @author $ {USERS}
 **/public interface GroupeRepesitory extends JpaRepository<Groupe, Long> {
    @Modifying
    @Query("UPDATE Groupe g SET g.membres = (SELECT m FROM g.membres m WHERE m.id <> :utilisateurId)")
    void retirerUtilisateurDeTousLesGroupes(@Param("utilisateurId") Long utilisateurId);
}
