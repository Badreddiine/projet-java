package com.example.javaprojet.repo;
import com.example.javaprojet.entity.Tache;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TacheRepesitory extends JpaRepository<Tache, Long> {
}
