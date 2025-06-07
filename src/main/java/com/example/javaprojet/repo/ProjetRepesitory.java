package com.example.javaprojet.repo;
import com.example.javaprojet.entity.Projet;

import com.example.javaprojet.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.example.javaprojet.enums.StatutProjet;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetRepository extends JpaRepository<Projet, Long> {

     // ========== REQUÊTES DE BASE ==========

     /**
      * Trouve un projet avec ses relations eagerly chargées
      */
     @Query("SELECT DISTINCT p FROM Projet p " +
             "LEFT JOIN FETCH p.membres " +
             "LEFT JOIN FETCH p.admins " +
             "LEFT JOIN FETCH p.groupe " +
             "WHERE p.id = :id")
     Optional<Projet> findByIdWithDetails(@Param("id") Long id);

     /**
      * Trouve un projet avec tous ses membres et admins
      */
     @Query("SELECT DISTINCT p FROM Projet p " +
             "LEFT JOIN FETCH p.membres " +
             "LEFT JOIN FETCH p.admins " +
             "WHERE p.id = :id")
     Optional<Projet> findByIdWithMembres(@Param("id") Long id);

     // ========== REQUÊTES PAR STATUT ET VISIBILITÉ ==========

     List<Projet> findByEstPublic(boolean estPublic);

     List<Projet> findByStatutProjet(StatutProjet statut);

     @Query("SELECT p FROM Projet p WHERE p.estPublic = true AND p.statutProjet = :statut")
     List<Projet> findPublicProjetsByStatut(@Param("statut") StatutProjet statut);

     // ========== REQUÊTES PAR UTILISATEUR ==========

     /**
      * Trouve tous les projets où l'utilisateur est admin principal
      */
//     @Query("SELECT p FROM Projet p WHERE p.mainAdmin.id = :adminId")
//     List<Projet> findByMainAdmin(@Param("adminId") Long adminId);

     /**
      * Trouve tous les projets où l'utilisateur est membre
      */
     @Query("SELECT DISTINCT p FROM Projet p JOIN p.membres m WHERE m.id = :utilisateurId")
     List<Projet> findByMembreId(@Param("utilisateurId") Long utilisateurId);

     /**
      * Trouve tous les projets où l'utilisateur est admin
      */
     @Query("SELECT DISTINCT p FROM Projet p JOIN p.admins a WHERE a.id = :utilisateurId")
     List<Projet> findByAdminId(@Param("utilisateurId") Long utilisateurId);

     /**
      * Trouve tous les projets accessibles par un utilisateur (membre ou public)
      */
     @Query("SELECT DISTINCT p FROM Projet p " +
             "WHERE p.estPublic = true OR " +
             "EXISTS (SELECT m FROM p.membres m WHERE m.id = :utilisateurId)")
     List<Projet> findProjetsAccessiblesByUser(@Param("utilisateurId") Long utilisateurId);

     // ========== REQUÊTES DE RECHERCHE ==========

     /**
      * Recherche de projets par mot-clé dans le nom ou la description
      */
     @Query("SELECT p FROM Projet p WHERE " +
             "LOWER(p.nomCourt) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
             "LOWER(p.nomLong) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
             "LOWER(p.description) LIKE LOWER(CONCAT('%', :motCle, '%'))")
     List<Projet> findByMotCle(@Param("motCle") String motCle);

     /**
      * Recherche de projets publics par mot-clé
      */
     @Query("SELECT p FROM Projet p WHERE p.estPublic = true AND (" +
             "LOWER(p.nomCourt) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
             "LOWER(p.nomLong) LIKE LOWER(CONCAT('%', :motCle, '%')) OR " +
             "LOWER(p.description) LIKE LOWER(CONCAT('%', :motCle, '%')))")
     List<Projet> findPublicProjetsByMotCle(@Param("motCle") String motCle);

     /**
      * Trouve les projets par thème
      */
     @Query("SELECT p FROM Projet p WHERE LOWER(p.theme) = LOWER(:theme)")
     List<Projet> findByTheme(@Param("theme") String theme);

     // ========== REQUÊTES POUR LES GROUPES ==========

     /**
      * Trouve les projets d'un groupe
      */
     @Query("SELECT p FROM Projet p WHERE p.groupe.id = :groupeId")
     List<Projet> findByGroupeId(@Param("groupeId") Long groupeId);

     /**
      * Trouve les projets d'un groupe avec un statut spécifique
      */
     @Query("SELECT p FROM Projet p WHERE p.groupe.id = :groupeId AND p.statutProjet = :statut")
     List<Projet> findByGroupeIdAndStatut(@Param("groupeId") Long groupeId, @Param("statut") StatutProjet statut);

     // ========== REQUÊTES POUR LES MEMBRES ET DEMANDES ==========

     /**
      * Vérifie si un utilisateur est membre d'un projet
      */
     @Query("SELECT COUNT(p) > 0 FROM Projet p JOIN p.membres m WHERE p.id = :projetId AND m.id = :utilisateurId")
     boolean isUserMemberOfProjet(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

     /**
      * Vérifie si un utilisateur est admin d'un projet
      */
     @Query("SELECT COUNT(p) > 0 FROM Projet p JOIN p.admins a WHERE p.id = :projetId AND a.id = :utilisateurId")
     boolean isUserAdminOfProjet(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

     /**
      * Vérifie si un utilisateur est l'admin principal d'un projet
      */
     @Query("SELECT COUNT(p) > 0 FROM Projet p WHERE p.id = :projetId  ")
     boolean isUserMainAdminOfProjet(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

     /**
      * Trouve les membres d'un projet si l'utilisateur a accès
      */
     @Query("SELECT m FROM Projet p JOIN p.membres m WHERE p.id = :projetId AND " +
             "(p.estPublic = true OR EXISTS (SELECT u FROM p.membres u WHERE u.id = :utilisateurId))")
     List<Utilisateur> findMembresIfUserHasAccess(@Param("projetId") Long projetId, @Param("utilisateurId") Long utilisateurId);

     /**
      * Trouve les demandeurs en attente d'un projet
      */
     @Query("SELECT d FROM Projet p JOIN p.demandeursEnAttente d WHERE p.id = :projetId")
     List<Utilisateur> findDemandeursEnAttente(@Param("projetId") Long projetId);

     // ========== REQUÊTES DE NETTOYAGE ==========

     /**
      * Supprime tous les rôles d'un projet
      */
     @Modifying
     @Query("DELETE FROM ProjetRole pr WHERE pr.projet.id = :projetId")
     void deleteRolesByProjetId(@Param("projetId") Long projetId);

     /**
      * Compte le nombre de projets par statut
      */
     @Query("SELECT p.statutProjet, COUNT(p) FROM Projet p GROUP BY p.statutProjet")
     List<Object[]> countProjetsByStatut();

     /**
      * Trouve les projets créés dans les X derniers jours
      */
     @Query("SELECT p FROM Projet p WHERE p.dateCreation >= CURRENT_DATE - :jours")
     List<Projet> findProjetsCreatedInLastDays(@Param("jours") int jours);

     // ========== REQUÊTES DE STATISTIQUES ==========

     /**
      * Compte le nombre total de membres pour un projet
      */
     @Query("SELECT COUNT(m) FROM Projet p JOIN p.membres m WHERE p.id = :projetId")
     Long countMembresByProjetId(@Param("projetId") Long projetId);

     /**
      * Compte le nombre total d'admins pour un projet
      */
     @Query("SELECT COUNT(a) FROM Projet p JOIN p.admins a WHERE p.id = :projetId")
     Long countAdminsByProjetId(@Param("projetId") Long projetId);
}

