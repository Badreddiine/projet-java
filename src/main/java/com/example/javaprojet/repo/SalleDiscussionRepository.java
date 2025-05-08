package com.example.javaprojet.repo;

import com.example.javaprojet.entity.SalleDiscussion;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.TypeSalle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SalleDiscussionRepository extends JpaRepository<SalleDiscussion, Long> {
    List<SalleDiscussion> findByProjet_Id(Long projetId);
    List<SalleDiscussion> findByGroupe_Id(Long groupeId);
    List<SalleDiscussion> findByMembresContaining(Utilisateur utilisateur);
    List<SalleDiscussion> findByTypeSalleAndMembresContaining(TypeSalle typeSalle, Utilisateur utilisateur);

    @Query("SELECT s FROM SalleDiscussion s WHERE s.typeSalle = :typeSalle AND :user1 MEMBER OF s.membres AND :user2 MEMBER OF s.membres AND SIZE(s.membres) = 2")
    List<SalleDiscussion> findByTypeAndMembres(@Param("typeSalle") TypeSalle typeSalle,
                                               @Param("user1") Utilisateur user1,
                                               @Param("user2") Utilisateur user2);
}
