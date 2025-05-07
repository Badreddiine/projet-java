package com.example.javaprojet.repo;

import com.example.javaprojet.entity.ProjetUtilisateurEnAttente;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProjetUtilisateurEnAttenteRepository extends JpaRepository<ProjetUtilisateurEnAttente, Long> {
    boolean existsByProjetIdAndUtilisateurId(Long projetId, Long utilisateurId);

    @Modifying
    @Query("DELETE FROM ProjetUtilisateurEnAttente p WHERE p.projet.id = :projetId AND p.utilisateur.id = :utilisateurId")
    void deleteByProjetAndUtilisateur(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);
}