package com.example.javaprojet.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;

@Setter
@Getter
@NoArgsConstructor // Ajoute un constructeur sans arguments
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private Long id;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;



    public static UserPrincipal create(Utilisateur utilisateur) {
        List<GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name()),
                new SimpleGrantedAuthority("ROLE_" + utilisateur.getRoleSecondaire().name())
        );

        return new UserPrincipal(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                authorities,
                new HashMap<>()
        );
    }

    public static UserPrincipal create(Utilisateur utilisateur, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(utilisateur);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }


    @Override
    public String getUsername() {
        return email; // Utilisez l'email comme nom d'utilisateur
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Compte toujours valide
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Compte jamais verrouillé
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Identifiants toujours valides
    }

    @Override
    public boolean isEnabled() {
        return true; // Compte toujours activé
    }
}