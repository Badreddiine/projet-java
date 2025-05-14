package com.example.javaprojet.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Entity
@Setter
@Getter
@NoArgsConstructor // Ajoute un constructeur sans arguments
public class UserPrincipal implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String email;
    private String password;

    @Transient // Ne pas persister cette collection dans la base de données
    private Collection<? extends GrantedAuthority> authorities;

    @Transient // Ne pas persister cette map dans la base de données
    private Map<String, Object> attributes;

    public UserPrincipal(Long id, String email, String password, Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }

    public static UserPrincipal create(Utilisateur utilisateur) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + utilisateur.getRole().name())
        );

        return new UserPrincipal(
                utilisateur.getId(),
                utilisateur.getEmail(),
                utilisateur.getMotDePasse(),
                authorities
        );
    }

    public static UserPrincipal create(Utilisateur utilisateur, Map<String, Object> attributes) {
        UserPrincipal userPrincipal = UserPrincipal.create(utilisateur);
        userPrincipal.setAttributes(attributes);
        return userPrincipal;
    }

    // Implémentation des méthodes manquantes de UserDetails

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