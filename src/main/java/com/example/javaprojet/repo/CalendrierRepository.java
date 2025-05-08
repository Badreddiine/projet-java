package com.example.javaprojet.repo;
import com.example.javaprojet.entity.Calendrier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface CalendrierRepository extends JpaRepository<Calendrier, Long> {
     List<Calendrier> findByNomContaining(String motCle);
}
