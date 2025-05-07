package com.example.javaprojet;

import com.example.javaprojet.entity.*;
import com.example.javaprojet.model.RoleType;
import com.example.javaprojet.repo.*;
import com.example.javaprojet.services.AdminService;
import com.example.javaprojet.services.RoleService;
import com.example.javaprojet.services.UtilisateurService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import java.util.List;
import java.util.Scanner;

@SpringBootApplication
public class JavaProjetApplication {

    public static void main(String[] args) {
        SpringApplication.run(JavaProjetApplication.class, args);
    }

    @Bean
    public CommandLineRunner demo(AdminService adminService,
                                  UtilisateurService utilisateurService,
                                  RoleService roleService,
                                  UtilisateurRepesitory userRepo,
                                  ProjetRepesitory projetRepo,
                                  GroupeRepesitory groupeRepo,
                                  AdminRepesitory adminRepo) {
        return args -> {
            Scanner scanner = new Scanner(System.in);
            displayWelcomeMessage();
            initTestData(userRepo, projetRepo, groupeRepo, adminRepo);

            mainLoop:
            while (true) {
                displayMainMenu();
                String input = scanner.nextLine().trim();

                switch (input) {
                    case "1":
                        userFunctionsMenu(utilisateurService, scanner);
                        break;
                    case "2":
                        adminFunctionsMenu(adminService, roleService, scanner);
                        break;
                    case "3":
                        System.out.println("\nAu revoir !");
                        break mainLoop;
                    default:
                        System.out.println("\nOption invalide. Veuillez réessayer.");
                }
            }
            scanner.close();
        };
    }

    private void displayWelcomeMessage() {
        System.out.println("\n=== Système de Gestion de Projets ===");
        System.out.println("=== Mode Test Console ===");
    }

    private void displayMainMenu() {
        System.out.println("\nOptions principales:");
        System.out.println("1. Fonctions Utilisateur");
        System.out.println("2. Fonctions Admin");
        System.out.println("3. Quitter");
        System.out.print("Votre choix: ");
    }

    private void initTestData(UtilisateurRepesitory userRepo,
                              ProjetRepesitory projetRepo,
                              GroupeRepesitory groupeRepo,
                              AdminRepesitory adminRepo) {
        try {
            // Création des utilisateurs tests
            Utilisateur user1 = createUser("jean.dupont", "Dupont", "Jean", "jean.dupont@email.com", "pass123");
            Utilisateur user2 = createUser("sophie.martin", "Martin", "Sophie", "sophie.martin@email.com", "pass456");

            // Création de l'admin
            Admin admin = createAdmin("admin.system", "Admin", "System", "admin@system.com", "admin123");

            userRepo.saveAll(List.of(user1, user2));
            adminRepo.save(admin);

            System.out.println("\nDonnées de test initialisées:");
            System.out.println("- 2 utilisateurs (jean.dupont, sophie.martin)");
            System.out.println("- 1 admin (admin.system)");
        } catch (Exception e) {
            System.err.println("Erreur lors de l'initialisation des données: " + e.getMessage());
        }
    }

    private Utilisateur createUser(String identifiant, String nom, String prenom, String email, String mdp) {
        Utilisateur user = new Utilisateur();
        user.setIdentifiant(identifiant);
        user.setNom(nom);
        user.setPrenom(prenom);
        user.setEmail(email);
        user.setMotDePasse(mdp);
        return user;
    }

    private Admin createAdmin(String identifiant, String nom, String prenom, String email, String mdp) {
        Admin admin = new Admin();
        admin.setIdentifiant(identifiant);
        admin.setNom(nom);
        admin.setPrenom(prenom);
        admin.setEmail(email);
        admin.setMotDePasse(mdp);
        return admin;
    }

    private void userFunctionsMenu(UtilisateurService service, Scanner scanner) {
        userMenuLoop:
        while (true) {
            System.out.println("\nFonctions Utilisateur:");
            System.out.println("1. Créer un compte");
            System.out.println("2. Se connecter");
            System.out.println("3. Demander création projet");
            System.out.println("4. Modifier profil");
            System.out.println("5. Lister projets publics");
            System.out.println("6. Demander à rejoindre un projet");
            System.out.println("7. Afficher mes informations");
            System.out.println("8. Retour au menu principal");
            System.out.print("Votre choix: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleAccountCreation(service, scanner);
                    break;
                case "2":
                    handleUserLogin(service, scanner);
                    break;
                case "3":
                    handleProjectRequest(service, scanner);
                    break;
                case "4":
                    handleProfileUpdate(service, scanner);
                    break;
                case "5":
                    listPublicProjects(service);
                    break;
                case "6":
                    handleJoinProjectRequest(service, scanner);
                    break;
                case "7":
                    handleAfficheInformation(service,scanner);
                    break ;
                case "8":
                    break userMenuLoop;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void handleAccountCreation(UtilisateurService service, Scanner scanner) {
        System.out.println("\nCréation de compte utilisateur:");

        Utilisateur newUser = new Utilisateur();

        System.out.print("Identifiant: ");
        newUser.setIdentifiant(scanner.nextLine());

        System.out.print("Nom: ");
        newUser.setNom(scanner.nextLine());

        System.out.print("Prénom: ");
        newUser.setPrenom(scanner.nextLine());

        System.out.print("Email: ");
        newUser.setEmail(scanner.nextLine());

        System.out.print("Mot de passe: ");
        newUser.setMotDePasse(scanner.nextLine());

        try {
            service.creeCompte(newUser);
            System.out.println("\nCompte créé avec succès !");
        } catch (Exception e) {
            System.err.println("\nErreur lors de la création du compte: " + e.getMessage());
        }
    }

    private void handleUserLogin(UtilisateurService service, Scanner scanner) {
        System.out.println("\nConnexion utilisateur:");

        System.out.print("Email: ");
        String email = scanner.nextLine();

        System.out.print("Mot de passe: ");
        String password = scanner.nextLine();

        try {
            boolean success = service.seConnecter(email, password);
            if (success) {
                System.out.println("\nConnexion réussie !");
            } else {
                System.out.println("\nÉchec de la connexion. Email ou mot de passe incorrect.");
            }
        } catch (Exception e) {
            System.err.println("\nErreur lors de la connexion: " + e.getMessage());
        }
    }

    private void handleProjectRequest(UtilisateurService service, Scanner scanner) {
        System.out.println("\nDemande de création de projet:");
        // Implémentation de la logique de demande de projet
    }

    private void handleProfileUpdate(UtilisateurService service, Scanner scanner) {
        System.out.println("\nModification de profil:");
        // Implémentation de la logique de modification de profil
    }

    private void listPublicProjects(UtilisateurService service) {
        System.out.println("\nListe des projets publics:");
        try {
            List<Projet> projets = service.listeProjets();
            if (projets.isEmpty()) {
                System.out.println("Aucun projet public disponible.");
            } else {
                projets.forEach(p -> System.out.println(
                        "- " + p.getNomLong() + " (" + p.getNomCourt() + ") - Statut: " + p.getEtat()
                ));
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la récupération des projets: " + e.getMessage());
        }
    }

    private void handleJoinProjectRequest(UtilisateurService service, Scanner scanner) {
        System.out.println("\nDemande pour rejoindre un projet:");
        // Implémentation de la logique de demande de participation
    }
    private void handleAfficheInformation(UtilisateurService service, Scanner scanner) {
        System.out.println("\n saisir your id:");
        long id = scanner.nextLong();
       service.afficherMesInformations(id);
    }

    private void adminFunctionsMenu(AdminService adminService, RoleService roleService, Scanner scanner) {
        adminMenuLoop:
        while (true) {
            System.out.println("\nFonctions Administrateur:");
            System.out.println("1. Accepter un projet");
            System.out.println("2. Refuser un projet");
            System.out.println("3. Supprimer un projet");
            System.out.println("4. Bannir un utilisateur");
            System.out.println("5. Vérifier les droits");
            System.out.println("6. Retour au menu principal");
            System.out.print("Votre choix: ");

            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    handleProjectApproval(adminService, scanner);
                    break;
                case "2":
                    handleProjectRejection(adminService, scanner);
                    break;
                case "3":
                    handleProjectDeletion(adminService, scanner);
                    break;
                case "4":
                    handleUserBan(adminService, scanner);
                    break;
                case "5":
                    checkPermissions(roleService, scanner);
                    break;
                case "6":
                    break adminMenuLoop;
                default:
                    System.out.println("Option invalide. Veuillez réessayer.");
            }
        }
    }

    private void handleProjectApproval(AdminService service, Scanner scanner) {
        System.out.println("\nAccepter un projet:");
        // Implémentation de la logique d'acceptation de projet
    }

    private void handleProjectRejection(AdminService service, Scanner scanner) {
        System.out.println("\nRefuser un projet:");
        // Implémentation de la logique de refus de projet
    }

    private void handleProjectDeletion(AdminService service, Scanner scanner) {
        System.out.println("\nSupprimer un projet:");
        // Implémentation de la logique de suppression de projet
    }

    private void handleUserBan(AdminService service, Scanner scanner) {
        System.out.println("\nBannir un utilisateur:");
        long id = scanner.nextLong();
        service.bannerUtilisateur(id);
    }

    private void checkPermissions(RoleService service, Scanner scanner) {
        System.out.println("\nVérification des droits:");
        // Implémentation de la vérification des permissions
    }
}
