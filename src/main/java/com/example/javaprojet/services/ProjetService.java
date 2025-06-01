package com.example.javaprojet.services;
import com.example.javaprojet.dto.ProjetDTO;
import com.example.javaprojet.entity.Projet;
import com.example.javaprojet.entity.Utilisateur;
import com.example.javaprojet.enums.StatutProjet;
import com.example.javaprojet.repo.ProjetRepesitory;
import com.example.javaprojet.repo.UtilisateurRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ProjetService {

    private final ProjetRepesitory projetRepository;
    private final UtilisateurRepository utilisateurRepository;

    /**
     *
     * @param projetDTO
     * @param admin
     * @return
     */
    public Projet creerProjet(ProjetDTO projetDTO, Utilisateur admin) {
        Projet projet = new Projet(projetDTO);
        projet.setDateCreation(new Date());
        projet.setStatutProjet(StatutProjet.EN_ATTENTE);
        projet.setMainAdmin(admin);
        return projetRepository.save(projet);
    }

    public Optional<Projet> getProjetById(Long id) {
        return projetRepository.findById(id);
    }

    public List<Projet> getAllProjets() {
        return projetRepository.findAll();
    }

    public List<Projet> getProjetsPublics() {
        return projetRepository.findAll().stream()
                .filter(Projet::isEstPublic)
                .collect(Collectors.toList());
    }

    public List<Projet> getProjetsByStatut(StatutProjet statut) {
        return projetRepository.findAll().stream()
                .filter(p -> p.getStatutProjet() == statut)
                .collect(Collectors.toList());
    }

    public List<Projet> getProjetsByTheme(String theme) {
        return projetRepository.findAll().stream()
                .filter(p -> theme.equals(p.getTheme()))
                .collect(Collectors.toList());
    }

    /**
     * a chsnger avec un seulle dto ProjetDTO nouvellesDonnees
     * @param projetExistant
     * @param nouvellesDonnees
     * @return
     */
    @Transactional
    public Projet mettreAJourProjet(Projet projetExistant, Projet nouvellesDonnees) {
        if (nouvellesDonnees.getNomCourt() != null) projetExistant.setNomCourt(nouvellesDonnees.getNomCourt());
        if (nouvellesDonnees.getNomLong() != null) projetExistant.setNomLong(nouvellesDonnees.getNomLong());
        if (nouvellesDonnees.getDescription() != null) projetExistant.setDescription(nouvellesDonnees.getDescription());
        if (nouvellesDonnees.getTheme() != null) projetExistant.setTheme(nouvellesDonnees.getTheme());
        if (nouvellesDonnees.getType() != null) projetExistant.setType(nouvellesDonnees.getType());
        if (nouvellesDonnees.getLicense() != null) projetExistant.setLicense(nouvellesDonnees.getLicense());
        projetExistant.setEstPublic(nouvellesDonnees.isEstPublic());
        return projetRepository.save(projetExistant);
    }


    public Projet accepterProjet(Long id) throws RuntimeException {
        Projet projet=projetRepository.findById(id).orElseThrow(()->new RuntimeException("Projet non trouv<UNK>"));
        projet.setStatutProjet(StatutProjet.ACCEPTER);
        projet.setDateAcceptation(new Date());
        projet.setDateRejet(null);
        return projetRepository.save(projet);
    }

    @Transactional
    public Projet rejeterProjet(Long id) throws RuntimeException {
        Projet projet=projetRepository.findById(id).orElseThrow(()->new RuntimeException("Projet non trouv<UNK>"));
        projet.setStatutProjet(StatutProjet.REFUSER);
        projet.setDateRejet(new Date());
        projet.setDateAcceptation(null);
        return projetRepository.save(projet);
    }


    public Projet cloturerProjet(Long id) throws RuntimeException {
        Projet projet=projetRepository.findById(id).orElseThrow(()->new RuntimeException("Projet non trouv<UNK>"));
        if (projet.getStatutProjet() != StatutProjet.ACCEPTER) {
            throw new RuntimeException("Seuls les projets acceptés peuvent être clôturés");
        }
        projet.setStatutProjet(StatutProjet.CLOTURE);
        projet.setDateCloture(new Date());
        return projetRepository.save(projet);
    }


    public Projet reactiverProjet(Long id) throws RuntimeException {
        Projet projet=projetRepository.findById(id).orElseThrow(()->new RuntimeException("Projet non trouv<UNK>"));
        if (projet.getStatutProjet() != StatutProjet.CLOTURE) {
            throw new RuntimeException("Seuls les projets clôturés peuvent être réactivés");
        }
        projet.setStatutProjet(StatutProjet.ACCEPTER);
        projet.setDateCloture(null);
        return projetRepository.save(projet);
    }

    @Transactional
    public void ajouterMembre(Long id , Long userid ) throws RuntimeException {
        Projet projet=projetRepository.findById(id).orElseThrow(()->new RuntimeException("Projet non trouv<UNK>"));
        Utilisateur utilisateur =utilisateurRepository.findUtilisateurById(userid);
        if (projet.getMembres().contains(utilisateur)) {
            throw new RuntimeException("L'utilisateur est déjà membre du projet");
        }
        projet.getMembres().add(utilisateur);
        projetRepository.save(projet);
    }

    @Transactional
    public void retirerMembre(Long id , Long userid) {
        Projet projet=projetRepository.findById(id).orElseThrow(()->new RuntimeException("Projet non trouv<UNK>"));
        Utilisateur utilisateur =utilisateurRepository.findUtilisateurById(userid);
        if (projet.getAdmin() != null && projet.getAdmin().getId().equals(utilisateur.getId())) {
            throw new RuntimeException("L'admin principal ne peut pas être retiré du projet");
        }
        projet.getMembres().remove(utilisateur);
        projet.getAdmins().remove(utilisateur);
        projet.getDemandeursEnAttente().remove(utilisateur);
        projetRepository.save(projet);
    }

    @Transactional
    public void promouvoirEnAdmin(Projet projet, Utilisateur utilisateur) {
        if (!projet.getMembres().contains(utilisateur)) {
            throw new RuntimeException("L'utilisateur doit être membre du projet pour devenir admin");
        }
        projet.getAdmins().add(utilisateur);
        projetRepository.save(projet);
    }

    @Transactional
    public void retrograderAdmin(Projet projet, Utilisateur utilisateur) {
        if (projet.getAdmin() != null && projet.getAdmin().getId().equals(utilisateur.getId())) {
            throw new RuntimeException("L'admin principal ne peut pas être rétrogradé");
        }
        projet.getAdmins().remove(utilisateur);
        projetRepository.save(projet);
    }

    @Transactional
    public void changerAdminPrincipal(Projet projet, Utilisateur nouvelAdmin) {
        if (!projet.getMembres().contains(nouvelAdmin)) {
            throw new RuntimeException("Le nouvel admin doit être membre du projet");
        }
        projet.setMainAdmin(nouvelAdmin);
        projetRepository.save(projet);
    }

    @Transactional
    public void promouvoirEnAdmin(Long projetId, Long utilisateurId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (!projet.getMembres().contains(utilisateur)) {
            throw new RuntimeException("L'utilisateur doit être membre du projet pour devenir admin");
        }
        projet.getAdmins().add(utilisateur);
        projetRepository.save(projet);
    }

    @Transactional
    public void retrograderAdmin(Long projetId, Long utilisateurId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (projet.getAdmin() != null && projet.getAdmin().getId().equals(utilisateurId)) {
            throw new RuntimeException("L'admin principal ne peut pas être rétrogradé");
        }
        projet.getAdmins().remove(utilisateur);
        projetRepository.save(projet);
    }

    @Transactional
    public void changerAdminPrincipal(Long projetId, Long nouvelAdminId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        Utilisateur nouvelAdmin = utilisateurRepository.findById(nouvelAdminId).orElseThrow(() -> new RuntimeException("Nouvel admin non trouvé"));
        if (!projet.getMembres().contains(nouvelAdmin)) {
            throw new RuntimeException("Le nouvel admin doit être membre du projet");
        }
        projet.setMainAdmin(nouvelAdmin);
        projetRepository.save(projet);
    }

    public Set<Utilisateur> getMembres(Long projetId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        return projet.getMembres();
    }

    public Set<Utilisateur> getAdmins(Long projetId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        return projet.getAdmins();
    }

    public Set<Utilisateur> getDemandesEnAttente(Long projetId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        return projet.getDemandeursEnAttente();
    }

    @Transactional
    public void traiterDemandeAdhesion(Long projetId, Long utilisateurId, boolean accepter) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        if (!projet.getDemandeursEnAttente().contains(utilisateur)) {
            throw new RuntimeException("Aucune demande en attente pour cet utilisateur");
        }
        projet.getDemandeursEnAttente().remove(utilisateur);
        if (accepter) {
            projet.getMembres().add(utilisateur);
        }
        projetRepository.save(projet);
    }

    public boolean peutVoirProjet(Long projetId, Long utilisateurId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        if (projet.isEstPublic()) {
            return true;
        }
        if (utilisateurId != null) {
            Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElse(null);
            if (utilisateur != null && projet.getMembres().contains(utilisateur)) {
                return true;
            }
        }
        return false;
    }

    public List<Projet> rechercherProjets(String motCle) {
        return projetRepository.findAll().stream()
                .filter(p ->
                        (p.getNomCourt() != null && p.getNomCourt().toLowerCase().contains(motCle.toLowerCase())) ||
                                (p.getNomLong() != null && p.getNomLong().toLowerCase().contains(motCle.toLowerCase())) ||
                                (p.getDescription() != null && p.getDescription().toLowerCase().contains(motCle.toLowerCase()))
                )
                .collect(Collectors.toList());
    }

    public Map<String, Object> getStatistiquesProjet(Long projetId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        Map<String, Object> stats = new HashMap<>();
        stats.put("nombreMembres", projet.getMembres().size());
        stats.put("nombreAdmins", projet.getAdmins().size());
        stats.put("nombreTaches", projet.getTaches() != null ? projet.getTaches().size() : 0);
        stats.put("nombreReunions", projet.getReunions() != null ? projet.getReunions().size() : 0);
        stats.put("nombreDocuments", projet.getDepotDocuments() != null ? projet.getDepotDocuments().size() : 0);
        stats.put("nombreDemandesEnAttente", projet.getDemandeursEnAttente().size());
        stats.put("dateCreation", projet.getDateCreation());
        stats.put("statut", projet.getStatutProjet());
        return stats;
    }

    @Transactional
    public void supprimerProjet(Long projetId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        projet.getMembres().clear();
        projet.getAdmins().clear();
        projet.getDemandeursEnAttente().clear();
        projetRepository.delete(projet);
    }

    public boolean aPermissionAdmin(Long projetId, Long utilisateurId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId).orElseThrow(() -> new RuntimeException("Utilisateur non trouvé"));
        return projet.getAdmins().contains(utilisateur);
    }

    public boolean estAdminPrincipal(Long projetId, Long utilisateurId) {
        Projet projet = projetRepository.findById(projetId).orElseThrow(() -> new RuntimeException("Projet non trouvé"));
        return projet.getAdmin() != null && projet.getAdmin().getId().equals(utilisateurId);
    }
}
