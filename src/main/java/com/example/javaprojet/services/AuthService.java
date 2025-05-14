package com.example.javaprojet.services;


import com.example.javaprojet.dto.AuthRequestDTO;
import com.example.javaprojet.dto.AuthResponseDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.MAPPERS.UtilisateurMapper;
import com.example.javaprojet.repo.UtilisateurRepesitory;
import com.example.javaprojet.security.JwtService;
import com.example.javaprojet.entity.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class AuthService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UtilisateurRepesitory utilisateurRepository;

    @Autowired
    private UtilisateurMapper utilisateurMapper;

    public AuthResponseDTO authenticateUser(AuthRequestDTO loginRequest) {
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

        // Mettre à jour l'état de connexion de l'utilisateur
        List<Utilisateur> users = utilisateurRepository.findByEmail(userPrincipal.getUsername());
        if (!users.isEmpty()) {
            Utilisateur utilisateur = users.get(0);
            utilisateur.setEstConnecte(true);
            utilisateur.setEstEnLigne(true);
            utilisateur.setDerniereConnexion(new Date());
            utilisateur.setRefreshToken(refreshToken);
            utilisateurRepository.save(utilisateur);

            return new AuthResponseDTO(
                    accessToken,
                    refreshToken,
                    "Bearer",
                    jwtService.extractExpiration(accessToken).getTime() - System.currentTimeMillis(),
                    utilisateurMapper.toDTO(utilisateur)
            );
        }

        throw new RuntimeException("Utilisateur non trouvé");
    }

    public AuthResponseDTO refreshToken(String refreshToken) {
        try {
            String username = jwtService.extractUsername(refreshToken);
            List<Utilisateur> users = utilisateurRepository.findByEmail(username);

            if (users.isEmpty() || !users.get(0).getRefreshToken().equals(refreshToken)) {
                throw new RuntimeException("Refresh token invalide");
            }

            Utilisateur utilisateur = users.get(0);
            UserPrincipal userPrincipal = UserPrincipal.create(utilisateur);

            String newAccessToken = jwtService.generateToken(userPrincipal);
            String newRefreshToken = jwtService.generateRefreshToken(userPrincipal);

            utilisateur.setRefreshToken(newRefreshToken);
            utilisateurRepository.save(utilisateur);

            return new AuthResponseDTO(
                    newAccessToken,
                    newRefreshToken,
                    "Bearer",
                    jwtService.extractExpiration(newAccessToken).getTime() - System.currentTimeMillis(),
                    utilisateurMapper.toDTO(utilisateur)
            );
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors du rafraîchissement du token: " + e.getMessage());
        }
    }

    public void logout() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<Utilisateur> users = utilisateurRepository.findByEmail(userPrincipal.getUsername());

            if (!users.isEmpty()) {
                Utilisateur utilisateur = users.get(0);
                utilisateur.setEstConnecte(false);
                utilisateur.setEstEnLigne(false);
                utilisateur.setRefreshToken(null);
                utilisateurRepository.save(utilisateur);
            }
        }

        SecurityContextHolder.clearContext();
    }

    public UtilisateurDTO getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
            List<Utilisateur> users = utilisateurRepository.findByEmail(userPrincipal.getUsername());

            if (!users.isEmpty()) {
                return utilisateurMapper.toDTO(users.get(0));
            }
        }

        throw new RuntimeException("Utilisateur non authentifié");
    }
}
