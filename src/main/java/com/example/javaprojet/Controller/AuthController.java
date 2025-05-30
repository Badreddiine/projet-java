package com.example.javaprojet.Controller;


import com.example.javaprojet.dto.AuthRequestDTO;
import com.example.javaprojet.dto.AuthResponseDTO;
import com.example.javaprojet.dto.RefreshTokenRequestDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.services.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.example.javaprojet.services.UtilisateurService;
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;

    public AuthController(AuthService authService, UtilisateurService utilisateurService) {
        this.authService = authService;
        this.utilisateurService = utilisateurService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@RequestBody AuthRequestDTO loginRequest) {
        try{
            return ResponseEntity.ok(authService.authenticateUser(loginRequest));
        } catch(Exception e){
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        authService.logout();
        return ResponseEntity.ok().build();
    }
    @PostMapping("/inscription")
    public ResponseEntity<String> creerCompte(@RequestBody Utilisateur utilisateur) {
        try {
            utilisateurService.creeCompte(utilisateur);
            return ResponseEntity.ok("Compte créé avec succès !");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("User not created");
        }

    }

    @GetMapping("/me")
    public ResponseEntity<UtilisateurDTO> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}

