package com.example.javaprojet.Controller;


import com.example.javaprojet.dto.AuthRequestDTO;
import com.example.javaprojet.dto.AuthResponseDTO;
import com.example.javaprojet.dto.RefreshTokenRequestDTO;
import com.example.javaprojet.dto.UtilisateurDTO;
import com.example.javaprojet.services.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@RequestBody AuthRequestDTO loginRequest) {
        return ResponseEntity.ok(authService.authenticateUser(loginRequest));
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

    @GetMapping("/me")
    public ResponseEntity<UtilisateurDTO> getCurrentUser() {
        return ResponseEntity.ok(authService.getCurrentUser());
    }
}

