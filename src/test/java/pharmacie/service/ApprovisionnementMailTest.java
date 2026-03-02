package pharmacie.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Medicament;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
class ApprovisionnementMailTest {

    @Autowired
    private ApprovisonnementService service;

    @Autowired
    private MedicamentRepository medicamentRepository;

    @Autowired
    private pharmacie.dao.FournisseurRepository fournisseurRepo;

    @Autowired
    private pharmacie.dao.CategorieRepository categorieRepo;

    @Test
    @Transactional
    void testForcerEnvoiMail() {
        System.out.println("PREPARATION DES DONNEES...");
        
        // 1. On prend le médicament 93 défini dans test_data.sql
        Medicament m = medicamentRepository.findById(93).orElseThrow();
        m.setUnitesEnStock(1); // Le niveau d'alerte est à 10
        medicamentRepository.saveAndFlush(m);

        // 2. Création et association d'un fournisseur à la catégorie 98
        pharmacie.entity.Categorie cat = categorieRepo.findById(98).orElseThrow();
        
        pharmacie.entity.Fournisseur f = new pharmacie.entity.Fournisseur();
        f.setCode("TEST1");
        f.setNom("Fournisseur Test");
        f.setEmail("votre_email_ici@gmail.com"); // Email pour le log
        f.getCategories().add(cat);
        fournisseurRepo.saveAndFlush(f);
        
        System.out.println("TEST UNITAIRE : Lancement de la procédure d'approvisionnement...");
        
        // 2. On lance la méthode métier qui doit détecter le sous-stock et envoyer le mail
        service.lancerProcedureApprovisionnement();
        
        System.out.println("TEST UNITAIRE : Terminé.");
    }
}
