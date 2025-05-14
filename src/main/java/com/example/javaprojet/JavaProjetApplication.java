package com.example.javaprojet;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.Arrays;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootApplication
public class JavaProjetApplication {

    public static void main(String[] args) {
        try {
            log.info("Démarrage de l'application...");
            ConfigurableApplicationContext context = SpringApplication.run(JavaProjetApplication.class, args);
            log.info("Application démarrée avec succès!");

            // Afficher tous les beans chargés pour débogage
            String[] beanNames = context.getBeanDefinitionNames();
            Arrays.sort(beanNames);
            log.info("Beans chargés ({}):", beanNames.length);
            for (String beanName : beanNames) {
                log.debug(" - {}", beanName);
            }

        } catch (Exception e) {
            log.error("Erreur lors du démarrage de l'application", e);
            Throwable cause = e.getCause();
            while (cause != null) {
                log.error("Causée par: {}", cause.getMessage());
                cause = cause.getCause();
            }
        }
    }
}