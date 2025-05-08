package com.example.javaprojet.repo;

import com.example.javaprojet.entity.Message;
import com.example.javaprojet.entity.SalleDiscussion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findBySalleOrderByDateEnvoiAsc(SalleDiscussion salle);
    List<Message> findBySalle_IdOrderByDateEnvoiAsc(Long salleId);
    List<Message> findByExpediteur_IdAndSalle_Id(Long expediteurId, Long salleId);
    List<Message> findByEstLuFalseAndSalle_Id(Long salleId);
}

