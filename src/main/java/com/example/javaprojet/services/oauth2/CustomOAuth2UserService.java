package com.example.javaprojet.services.oauth2;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.model.Role;
import com.example.javaprojet.repo.UtilisateurRepesitory;
import com.example.javaprojet.security.UserPrincipal;
import com.example.javaprojet.security.oauth2.user.OAuth2UserInfo;
import com.example.javaprojet.security.oauth2.user.OAuth2UserInfoFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UtilisateurRepesitory utilisateurRepesitory;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest oAuth2UserRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(oAuth2UserRequest);

        try {
            return processOAuth2User(oAuth2UserRequest, oAuth2User);
        } catch (Exception ex) {
            throw new InternalAuthenticationServiceException(ex.getMessage(), ex.getCause());
        }
    }

    private OAuth2User processOAuth2User(OAuth2UserRequest oAuth2UserRequest, OAuth2User oAuth2User) {
        String registrationId = oAuth2UserRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo oAuth2UserInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(registrationId, oAuth2User.getAttributes());

        if(!StringUtils.hasText(oAuth2UserInfo.getEmail())) {
            throw new OAuth2AuthenticationException("Email introuvable depuis le fournisseur OAuth2");
        }

        List<Utilisateur> userList = utilisateurRepesitory.findByEmail(oAuth2UserInfo.getEmail());
        Utilisateur utilisateur;

        if (!userList.isEmpty()) {
            utilisateur = userList.get(0);
            if (utilisateur.getProvider() != null && !utilisateur.getProvider().equals(registrationId)) {
                throw new OAuth2AuthenticationException("Vous êtes inscrit avec " +
                        utilisateur.getProvider() + ". Veuillez utiliser ce fournisseur pour vous connecter.");
            }
            utilisateur = updateExistingUser(utilisateur, oAuth2UserInfo);
        } else {
            utilisateur = registerNewUser(oAuth2UserRequest, oAuth2UserInfo);
        }

        return UserPrincipal.create(utilisateur, oAuth2User.getAttributes());
    }

    private Utilisateur registerNewUser(OAuth2UserRequest oAuth2UserRequest, OAuth2UserInfo oAuth2UserInfo) {
        Utilisateur utilisateur = new Utilisateur();

        utilisateur.setProvider(oAuth2UserRequest.getClientRegistration().getRegistrationId());
        utilisateur.setProviderId(oAuth2UserInfo.getId());
        utilisateur.setNom(oAuth2UserInfo.getName());
        utilisateur.setPrenom(oAuth2UserInfo.getName().split(" ")[0]); // Simplification, à adapter
        utilisateur.setEmail(oAuth2UserInfo.getEmail());
        utilisateur.setIdentifiant(UUID.randomUUID().toString().substring(0, 8));
        utilisateur.setActif(true);
        utilisateur.setEstConnecte(true);
        utilisateur.setEstEnLigne(true);
        utilisateur.setRole(Role.USER);
        utilisateur.setDateInscription(new Date());
        utilisateur.setAvatar(oAuth2UserInfo.getImageUrl());

        return utilisateurRepesitory.save(utilisateur);
    }

    private Utilisateur updateExistingUser(Utilisateur utilisateur, OAuth2UserInfo oAuth2UserInfo) {
        utilisateur.setNom(oAuth2UserInfo.getName());
        utilisateur.setAvatar(oAuth2UserInfo.getImageUrl());
        utilisateur.setEstConnecte(true);
        utilisateur.setEstEnLigne(true);
        utilisateur.setDerniereConnexion(new Date());

        return utilisateurRepesitory.save(utilisateur);
    }
}

