package com.example.javaprojet.repo;



import com.example.javaprojet.entity.ListeDiffusion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ListeDiffusionRepository extends JpaRepository<ListeDiffusion, Long> {
}
