package pharmacie.entity;

import java.util.LinkedList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.*;

@Entity
@Getter @Setter @NoArgsConstructor @RequiredArgsConstructor @ToString
public class Fournisseur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Setter(AccessLevel.NONE)
    private Integer id;

    @NonNull
    @Size(min = 1, max = 255)
    @Column(length = 255)
    @NotBlank
    private String nom;

    @NonNull
    @Email
    @Size(max = 255)
    @Column(length = 255)
    private String email;

    @ToString.Exclude
    @ManyToMany
    @JsonIgnoreProperties("fournisseurs")
    private List<Categorie> categories = new LinkedList<>();
}