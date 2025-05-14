package com.example.javaprojet.repo;

import com.example.javaprojet.entity.SousTache;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
/**
 * @author $ {USERS}
 **/
public interface SousTacheRepesitory extends JpaRepository<SousTache, Long> {
   List<SousTache> findByTache_Id(Long tacheId);
    List<SousTache> findByEtat(String etat);
}
