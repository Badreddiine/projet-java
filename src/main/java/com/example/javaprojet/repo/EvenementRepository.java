package com.example.javaprojet.repo;


import com.example.javaprojet.entity.Evenement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository Spring Data JPA pour l'entité Evenement.
 * Hérite des méthodes CRUD de JpaRepository.
 */
@Repository
public interface EvenementRepository extends JpaRepository<Evenement, Long> {
    List<Evenement> findByTitreContaining(String motCle);
}
