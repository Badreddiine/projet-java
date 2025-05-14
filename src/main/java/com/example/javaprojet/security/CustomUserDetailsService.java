package com.example.javaprojet.security;

import com.example.javaprojet.entity.UserPrincipal;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.UtilisateurRepesitory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UtilisateurRepesitory utilisateurRepesitory;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        List<Utilisateur> users = utilisateurRepesitory.findByEmail(email);
        if (users.isEmpty()) {
            throw new UsernameNotFoundException("Utilisateur introuvable avec l'email : " + email);
        }

        Utilisateur utilisateur = users.get(0);
        return UserPrincipal.create(utilisateur);
    }

    @Transactional
    public UserDetails loadUserById(Long id) {
        Utilisateur utilisateur = utilisateurRepesitory.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur introuvable avec l'id : " + id));

        return UserPrincipal.create(utilisateur);
    }
}


