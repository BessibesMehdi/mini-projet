package pharmacie.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import pharmacie.entity.Fournisseur;
import java.util.List;

//interger en string pr SQL
public interface FournisseurRepository extends JpaRepository<Fournisseur, String> {
    
    
    List<Fournisseur> findByNomContainingIgnoreCase(String nom);

    
    List<Fournisseur> findByEmail(String email);
}