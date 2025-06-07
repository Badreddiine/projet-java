package com.example.javaprojet.services;

import com.example.javaprojet.dto.AuthRequestDTO;
import com.example.javaprojet.dto.AuthResponseDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.UtilisateurRepository;
import com.example.javaprojet.security.JwtService;
import com.example.javaprojet.entity.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AuthService {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    public AuthResponseDTO authenticateUser(AuthRequestDTO loginRequest) throws RuntimeException {
        try {
            logger.debug("Tentative d'authentification pour l'utilisateur: {}", loginRequest.getEmail());

            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            String accessToken = jwtService.generateToken(userPrincipal);
            String refreshToken = jwtService.generateRefreshToken(userPrincipal);

            // Rechercher l'utilisateur de manière sécurisée
            Optional<Utilisateur> optionalUser = findUserByEmail(userPrincipal.getUsername());

            if (optionalUser.isEmpty()) {
                logger.error("Utilisateur non trouvé après authentification réussie: {}", userPrincipal.getUsername());
                throw new RuntimeException("Utilisateur non trouvé");
            }

            Utilisateur utilisateur = optionalUser.get();

            // Mettre à jour l'état de connexion de l'utilisateur
            utilisateur.setEstConnecte(true);
            utilisateur.setEstEnLigne(true);
            utilisateur.setDerniereConnexion(new Date());
            utilisateur.setRefreshToken(refreshToken);

            try {
                utilisateurRepository.save(utilisateur);
                logger.debug("État de connexion mis à jour pour l'utilisateur: {}", utilisateur.getEmail());
            } catch (Exception e) {
                logger.error("Erreur lors de la sauvegarde de l'utilisateur: {}", e.getMessage(), e);
                throw new RuntimeException("Erreur lors de la mise à jour de l'utilisateur");
            }

            // Utiliser le constructeur de UtilisateurDTO qui prend un Utilisateur
            UtilisateurDTO utilisateurDTO = new UtilisateurDTO(utilisateur);

            logger.info("Authentification réussie pour l'utilisateur: {}", loginRequest.getEmail());

            return new AuthResponseDTO(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis(),
                    utilisateurDTO
            );

        } catch (AuthenticationException e) {
            logger.warn("Échec d'authentification pour l'utilisateur {}: {}", loginRequest.getEmail(), e.getMessage());
            throw new RuntimeException("Identifiants invalides");
        } catch (RuntimeException e) {
            // Re-lancer les RuntimeException déjà traitées
            throw e;
        } catch (Exception e) {
            logger.error("Erreur interne lors de l'authentification pour {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Erreur interne lors de l'authentification");
        }
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        try {
            logger.debug("Tentative de rafraîchissement de token");

            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new RuntimeException("Refresh token manquant");
            }

            String username = jwtService.extractUsername(refreshToken);
            logger.debug("Rafraîchissement de token pour l'utilisateur: {}", username);

            Optional<Utilisateur> optionalUser = findUserByEmail(username);

            if (optionalUser.isEmpty()) {
                logger.warn("Utilisateur non trouvé lors du rafraîchissement de token: {}", username);
                throw new RuntimeException("Utilisateur non trouvé");
            }

            Utilisateur utilisateur = optionalUser.get();
            String storedRefreshToken = utilisateur.getRefreshToken();

            // Vérification sécurisée du refresh token
            if (storedRefreshToken == null) {
                logger.warn("Aucun refresh token stocké pour l'utilisateur: {}", username);
                throw new RuntimeException("Refresh token invalide");
            }

            if (!storedRefreshToken.equals(refreshToken)) {
                logger.warn("Refresh token ne correspond pas pour l'utilisateur: {}", username);
                throw new RuntimeException("Refresh token invalide");
            }

            UserPrincipal userPrincipal = UserPrincipal.create(utilisateur);

            String newAccessToken = jwtService.generateToken(userPrincipal);
            String newRefreshToken = jwtService.generateRefreshToken(userPrincipal);

            utilisateur.setRefreshToken(newRefreshToken);

            try {
                utilisateurRepository.save(utilisateur);
                logger.debug("Nouveau refresh token sauvegardé pour l'utilisateur: {}", username);
            } catch (Exception e) {
                logger.error("Erreur lors de la sauvegarde du nouveau refresh token: {}", e.getMessage(), e);
                throw new RuntimeException("Erreur lors de la sauvegarde du token");
            }

            // Utiliser le constructeur de UtilisateurDTO qui prend un Utilisateur
            UtilisateurDTO utilisateurDTO = new UtilisateurDTO(utilisateur);

            logger.info("Token rafraîchi avec succès pour l'utilisateur: {}", username);

            return new AuthResponseDTO(
                    newAccessToken,
                    newRefreshToken,
                    "Bearer",
                    jwtService.extractExpiration(newAccessToken).getTime() - System.currentTimeMillis(),
                    utilisateurDTO
            );
        } catch (RuntimeException e) {
            // Re-lancer les RuntimeException déjà traitées
            throw e;
        } catch (Exception e) {
            logger.error("Erreur lors du rafraîchissement du token: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors du rafraîchissement du token: " + e.getMessage());
        }
    }

    public void logout() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                logger.debug("Déconnexion de l'utilisateur: {}", userPrincipal.getUsername());

                Optional<Utilisateur> optionalUser = findUserByEmail(userPrincipal.getUsername());

                if (optionalUser.isPresent()) {
                    Utilisateur utilisateur = optionalUser.get();
                    utilisateur.setEstConnecte(false);
                    utilisateur.setEstEnLigne(false);
                    utilisateur.setRefreshToken(null);

                    try {
                        utilisateurRepository.save(utilisateur);
                        logger.info("Utilisateur déconnecté avec succès: {}", userPrincipal.getUsername());
                    } catch (Exception e) {
                        logger.error("Erreur lors de la sauvegarde de la déconnexion: {}", e.getMessage(), e);
                    }
                } else {
                    logger.warn("Utilisateur non trouvé lors de la déconnexion: {}", userPrincipal.getUsername());
                }
            }

            SecurityContextHolder.clearContext();
        } catch (Exception e) {
            logger.error("Erreur lors de la déconnexion: {}", e.getMessage(), e);
            // Ne pas lancer d'exception pour la déconnexion, juste nettoyer le contexte
            SecurityContextHolder.clearContext();
        }
    }

    public UtilisateurDTO getCurrentUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
                UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
                Optional<Utilisateur> optionalUser = findUserByEmail(userPrincipal.getUsername());

                if (optionalUser.isPresent()) {
                    return new UtilisateurDTO(optionalUser.get());
                } else {
                    logger.warn("Utilisateur authentifié mais non trouvé en base: {}", userPrincipal.getUsername());
                }
            }

            logger.warn("Tentative d'accès à l'utilisateur courant sans authentification");
            throw new RuntimeException("Utilisateur non authentifié");
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            logger.error("Erreur lors de la récupération de l'utilisateur courant: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de la récupération de l'utilisateur");
        }
    }

    /**
     * Méthode utilitaire pour rechercher un utilisateur par email de manière sécurisée
     */
    private Optional<Utilisateur> findUserByEmail(String email) {
        try {
            List<Utilisateur> users = utilisateurRepository.findByEmail(email);

            if (users.isEmpty()) {
                return Optional.empty();
            }

            if (users.size() > 1) {
                logger.warn("Plusieurs utilisateurs trouvés avec le même email: {}. Utilisation du premier.", email);
            }

            return Optional.of(users.get(0));
        } catch (Exception e) {
            logger.error("Erreur lors de la recherche de l'utilisateur par email {}: {}", email, e.getMessage(), e);
            return Optional.empty();
        }
    }
}