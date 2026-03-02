package pharmacie.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pharmacie.dao.FournisseurRepository;
import pharmacie.dao.MedicamentRepository;
import pharmacie.entity.Categorie;
import pharmacie.entity.Fournisseur;
import pharmacie.entity.Medicament;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.SimpleMailMessage;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ApprovisonnementService {

    @Autowired
    private MedicamentRepository medicamentRepo;

    @Autowired
    private FournisseurRepository fournisseurRepo;

    @Autowired
    private JavaMailSender mailSender;

    @org.springframework.beans.factory.annotation.Value("${mailgun.sender:miniprojet@sandbox325cde38c0694d6dacec14bc062c6f49.mailgun.org}")
    private String mailSenderAddress;

    public void lancerProcedureApprovisionnement() {
        System.out.println("----- DEBUT PROCEDURE APPROVISIONNEMENT -----");
        // 1. Déterminer les médicaments en sous-stock
        List<Medicament> aReapprovisionner = medicamentRepo.findAll().stream()
                .filter(m -> m.getUnitesEnStock() < m.getNiveauDeReappro())
                .collect(Collectors.toList());

        System.out.println("Nb medicaments en sous-stock: " + aReapprovisionner.size());
        if (aReapprovisionner.isEmpty()) return;

        // 2. Récupérer tous les fournisseurs
        List<Fournisseur> tousLesFournisseurs = fournisseurRepo.findAll();
        System.out.println("Nb fournisseurs trouvés: " + tousLesFournisseurs.size());

        // 3. Pour chaque fournisseur, filtrer les médicaments qu'il peut fournir
        for (Fournisseur f : tousLesFournisseurs) {
            
            // Liste des catégories gérées par ce fournisseur
            List<Integer> codesCategoriesFournisseur = f.getCategories().stream()
                    .map(Categorie::getCode)
                    .collect(Collectors.toList());

            System.out.println("Fournisseur " + f.getCode() + " gere categories: " + codesCategoriesFournisseur);

            // Médicaments en alerte appartenant à ces catégories
            List<Medicament> medicamentsPourCeFournisseur = aReapprovisionner.stream()
                    .filter(m -> codesCategoriesFournisseur.contains(m.getCategorie().getCode()))
                    .collect(Collectors.toList());

            System.out.println("Fournisseur " + f.getCode() + " va recevoir " + medicamentsPourCeFournisseur.size() + " medicaments");

            if (!medicamentsPourCeFournisseur.isEmpty()) {
                envoyerMailFournisseur(f, medicamentsPourCeFournisseur);
            }
        }
        System.out.println("----- FIN PROCEDURE APPROVISIONNEMENT -----");
    }

    private void envoyerMailFournisseur(Fournisseur f, List<Medicament> medicaments) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(f.getEmail());
        // L'expéditeur est désormais injecté via la configuration Spring
        message.setFrom(mailSenderAddress);  
        message.setSubject("Demande de devis de réapprovisionnement - Pharmacie");

        StringBuilder corps = new StringBuilder();
        corps.append("Bonjour ").append(f.getNom()).append(",\n\n");
        corps.append("Merci de nous transmettre un devis pour les médicaments suivants :\n\n");

        // Regroupement par catégorie pour le mail
        Map<String, List<Medicament>> parCategorie = medicaments.stream()
                .collect(Collectors.groupingBy(m -> m.getCategorie().getLibelle()));

        parCategorie.forEach((libelle, liste) -> {
            corps.append("--- Catégorie : ").append(libelle).append(" ---\n");
            for (Medicament m : liste) {
                int quantiteAMander = m.getNiveauDeReappro() * 2 - m.getUnitesEnStock(); // Logique d'exemple
                corps.append("- ").append(m.getNom())
                     .append(" (Stock actuel: ").append(m.getUnitesEnStock())
                     .append(", Niveau alerte: ").append(m.getNiveauDeReappro()).append(")\n");
            }
            corps.append("\n");
        });

        corps.append("Cordialement,\nLa gestion de la pharmacie.");
        
        message.setText(corps.toString());
        
        try {
            System.out.println("Tentative d'envoi d'un email à : " + f.getEmail());
            mailSender.send(message);
            System.out.println("Email envoyé avec succès à : " + f.getEmail());
        } catch (Exception e) {
            System.err.println("ERREUR LORS DE L'ENVOI DE L'EMAIL à " + f.getEmail());
            e.printStackTrace();
        }
    }
}