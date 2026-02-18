package pharmacie.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pharmacie.service.ApprovisonnementService;

@RestController
@RequestMapping("/api/approvisionnement")
public class ApprovisonnementController {

    @Autowired
    private ApprovisonnementService approService;

    @PostMapping("/lancer")
    public String lancer() {
        approService.lancerProcedureApprovisionnement();
        return "Procédure de réapprovisionnement lancée. Les mails ont été envoyés aux fournisseurs concernés.";
    }
}