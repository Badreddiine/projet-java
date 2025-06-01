package com.example.javaprojet.security;

import com.example.javaprojet.entity.UserPrincipal;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.UtilisateurRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@Transactional
public class CustomUserDetailsService implements UserDetailsService {

    private final UtilisateurRepository utilisateurRepository;

    public CustomUserDetailsService(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Utilisateur> users = utilisateurRepository.findDistinctByEmail(email);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email);
        }

        Utilisateur utilisateur = users.get();
        return UserPrincipal.create(utilisateur);
    }
}


