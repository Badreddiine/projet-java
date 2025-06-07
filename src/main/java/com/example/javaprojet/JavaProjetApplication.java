package com.example.javaprojet;

import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.UtilisateurRepository;
import com.example.javaprojet.services.UtilisateurService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class JavaProjetApplication {
    public static void main(String[] args) {
        SpringApplication.run(JavaProjetApplication.class, args);
    }
}