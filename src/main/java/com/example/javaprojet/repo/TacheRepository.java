package com.example.javaprojet.repo;
import com.example.javaprojet.entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TacheRepository extends JpaRepository<Tache, Long> {
    List<Tache> findByEtat(String etat);
    List<Tache> findByAssigneA_Id(Long utilisateurId);
    List<Tache> findByProjet_Id(Long projetId);
}
