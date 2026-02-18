package pharmacie.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.LinkedList;
import java.util.List;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
public class Fournisseur {

    @Id
    @NonNull
    @Size(min = 1, max = 5)
    @Column(nullable = false, length = 5)
    private String code;

    @NonNull
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String nom;

    @NonNull
    @NotBlank
    @Email // Consigne : "adresse électronique"
    @Column(nullable = false, unique = true)
    private String email;

    @Size(max = 50)
    private String contact;

    @Embedded // Réutilisation de ton AdressePostale.java
    private AdressePostale adresse;

    @Size(max = 24)
    private String telephone;

    @Size(max = 24)
    private String fax;


@ManyToMany
@JoinTable(
    name = "fournisseur_categorie", // Doit correspondre dans le SQL
    joinColumns = @JoinColumn(name = "fournisseur_code"),
    inverseJoinColumns = @JoinColumn(name = "categorie_code")
)
private List<Categorie> categories = new LinkedList<>();
}