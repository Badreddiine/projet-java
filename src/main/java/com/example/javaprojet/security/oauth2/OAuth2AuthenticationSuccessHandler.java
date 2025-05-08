package com.example.javaprojet.security.oauth2;


import com.example.javaprojet.config.AppProperties;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.repo.UtilisateurRepesitory;
import com.example.javaprojet.security.JwtService;
import com.example.javaprojet.security.UserPrincipal;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;
import java.net.URI;
import java.util.Date;
import java.util.List;

@Component
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    @Autowired
    private JwtService jwtService;

    @Autowired
    private AppProperties appProperties;

    @Autowired
    private UtilisateurRepesitory utilisateurRepesitory;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
            throws IOException, ServletException {
        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            logger.debug("La réponse a déjà été commit. Impossible de rediriger vers " + targetUrl);
            return;
        }

        // Mettre à jour l'état de connexion de l'utilisateur
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        List<Utilisateur> users = utilisateurRepesitory.findByEmail(userPrincipal.getUsername());
        if (!users.isEmpty()) {
            Utilisateur utilisateur = users.get(0);
            utilisateur.setEstConnecte(true);
            utilisateur.setEstEnLigne(true);
            utilisateur.setDerniereConnexion(new Date());
            utilisateurRepesitory.save(utilisateur);
        }

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }

    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String redirectUri = request.getParameter("redirect_uri");

        if (!isAuthorizedRedirectUri(redirectUri)) {
            redirectUri = appProperties.getOauth2().getDefaultRedirectUri();
        }

        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        String token = jwtService.generateToken(userPrincipal);
        String refreshToken = jwtService.generateRefreshToken(userPrincipal);

        return UriComponentsBuilder.fromUriString(redirectUri)
                .queryParam("token", token)
                .queryParam("refresh_token", refreshToken)
                .build().toUriString();
    }

    private boolean isAuthorizedRedirectUri(String uri) {
        if (uri == null) {
            return false;
        }

        URI clientRedirectUri = URI.create(uri);

        return appProperties.getOauth2().getAuthorizedRedirectUris()
                .stream()
                .anyMatch(authorizedRedirectUri -> {
                    URI authorizedURI = URI.create(authorizedRedirectUri);
                    return authorizedURI.getHost().equalsIgnoreCase(clientRedirectUri.getHost())
                            && authorizedURI.getPort() == clientRedirectUri.getPort();
                });
    }
}


