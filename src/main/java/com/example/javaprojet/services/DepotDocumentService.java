package com.example.javaprojet.services;



import com.example.javaprojet.dto.DepotDocumentDTO;
import com.example.javaprojet.entity.DepotDocument;
import com.example.javaprojet.repo.DepotDocumentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class DepotDocumentService {

    private final DepotDocumentRepository depotDocumentRepository;

    @Autowired
    public DepotDocumentService(DepotDocumentRepository depotDocumentRepository) {
        this.depotDocumentRepository = depotDocumentRepository;
    }

    /**
     * methode our cree un depot document
     * @param document il faut recuprirer entite de dto dans le controlleur
     * @return
     */
    public DepotDocument create(DepotDocument document) {

        return depotDocumentRepository.save(document);
    }

    // ✅ Lire un document par ID
    @Transactional(readOnly = true)
    public Optional<DepotDocument> findById(Long id) {

        return depotDocumentRepository.findById(id);
    }

    /**
     * il faut recuprer id du dto dans controlleur
     * @return
     */
    @Transactional(readOnly = true)
    public List<DepotDocument> findAll() {

        return depotDocumentRepository.findAll();
    }

    /**
     * methode pour modifier un depot de document
     * @param id recupere de dto envoyer dans controlleur
     * @param updated dto qui contient les nvs champs
     * @return
     */
    public DepotDocument update(Long id, DepotDocumentDTO updated) {
        return depotDocumentRepository.findById(id)
                .map(existing -> {
                    existing.setNom(updated.getNom());
                    existing.setType(updated.getType());
                    existing.setChemin(updated.getChemin());
                    existing.setDateCreation(updated.getDateCreation());
                    existing.setEstPublic(updated.isEstPublic());
                    existing.setProjet(updated.getProjet());
                    return depotDocumentRepository.save(existing);
                })
                .orElseThrow(() -> new RuntimeException("Document non trouvé avec id " + id));
    }

    /**
     * metjode pour suprimer une entitee depoddocument
     * @param id ilfaut le recuperer du dto dans controller
     */
    public void delete(Long id) {
        if (!depotDocumentRepository.existsById(id)) {
            throw new RuntimeException("Document non trouvé avec id " + id);
        }
        depotDocumentRepository.deleteById(id);
    }
}
