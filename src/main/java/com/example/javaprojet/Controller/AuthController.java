package com.example.javaprojet.Controller;

import com.example.javaprojet.dto.AuthRequestDTO;
import com.example.javaprojet.dto.AuthResponseDTO;
import com.example.javaprojet.dto.RefreshTokenRequestDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.entity.MessageResponse;
import com.example.javaprojet.services.AuthService;
import com.example.javaprojet.services.UtilisateurService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UtilisateurService utilisateurService;

    public AuthController(AuthService authService, UtilisateurService utilisateurService) {
        this.authService = authService;
        this.utilisateurService = utilisateurService;
    }
    @PostMapping("/test")
    public ResponseEntity<Map<String, String>> testPost(@RequestBody(required = false) Map<String, Object> body) {
        Map<String, String> response = new HashMap<>();
        response.put("message", "✅ POST request works!");
        response.put("status", "success");
        response.put("receivedData", body != null ? body.toString() : "no data");
        return ResponseEntity.ok(response);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody AuthRequestDTO loginRequest) {
        try {
            return ResponseEntity.ok(authService.authenticateUser(loginRequest));
        } catch (BadCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshToken(@Valid @RequestBody RefreshTokenRequestDTO refreshTokenRequest) {
        return ResponseEntity.ok(authService.refreshToken(refreshTokenRequest.getRefreshToken()));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logoutUser() {
        authService.logout();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/inscription")
    public ResponseEntity<?> creerCompte(@Valid @RequestBody UtilisateurDTO utilisateurDTO) {
        try {
            // 1. IMPORTANT : On garde le mot de passe en clair dans une variable
            // car utilisateurService.creerUtilisateur va probablement le hacher (crypter)
            // dans l'objet utilisateurDTO ou en base.
            String motDePasseEnClair = utilisateurDTO.getMotDePasse();

            // 2. On crée l'utilisateur
            utilisateurService.creerUtilisateur(utilisateurDTO);

            // 3. AUTO-LOGIN : On utilise le mot de passe EN CLAIR qu'on a sauvegardé
            AuthRequestDTO loginRequest = new AuthRequestDTO();
            loginRequest.setEmail(utilisateurDTO.getEmail());
            loginRequest.setPassword(motDePasseEnClair); // <-- On utilise la version non-hachée

            // 4. On génère les tokens
            AuthResponseDTO tokens = authService.authenticateUser(loginRequest);

            return ResponseEntity.ok(tokens);

        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new MessageResponse(e.getMessage()));
        } catch (Exception e) {
            // C'est ici que l'erreur se produisait.
            // On l'affiche dans la console pour comprendre le problème.
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new MessageResponse("Erreur technique : " + e.getMessage()));
        }
    }
    @GetMapping("/me")
    public ResponseEntity<UtilisateurDTO> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}
