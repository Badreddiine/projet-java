package com.example.javaprojet.repo;

import com.example.javaprojet.entity.DepotDocument;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DepotDocumentRepository extends JpaRepository<DepotDocument, Long> {
}