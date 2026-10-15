package fr.greenmove.facturation.infrastructure.persistance;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

/**
 * La ligne de la table {@code facture} : une classe technique, <b>séparée de l'agrégat</b> {@code Facture}.
 *
 * <p>Les annotations JPA vivent ici et nulle part ailleurs : le domaine n'en voit aucune. La location est une
 * simple colonne texte {@code location_id}, jamais une relation JPA vers la table {@code location} : chaque
 * hexagone a ses tables. La contrainte d'unicité sur {@code location_id} garantit aussi en base
 * « une facture par location ».</p>
 */
@Entity
@Table(name = "facture", uniqueConstraints = @UniqueConstraint(name = "uk_facture_location", columnNames = "location_id"))
public class FactureEntite {

    @Id
    @Column(name = "id", nullable = false, length = 32)
    private String id;

    @Column(name = "location_id", nullable = false, length = 64)
    private String idLocation;

    @Column(name = "usager_id", nullable = false, length = 64)
    private String idUsager;

    @Column(name = "montant_centimes", nullable = false)
    private long montantCentimes;

    @Column(name = "statut", nullable = false, length = 16)
    private String statut;

    @Column(name = "reference_paiement", length = 64)
    private String referencePaiement;

    /** Exigé par JPA. */
    protected FactureEntite() {
    }

    public FactureEntite(String id, String idLocation, String idUsager, long montantCentimes, String statut,
                         String referencePaiement) {
        this.id = id;
        this.idLocation = idLocation;
        this.idUsager = idUsager;
        this.montantCentimes = montantCentimes;
        this.statut = statut;
        this.referencePaiement = referencePaiement;
    }

    public String getId() {
        return id;
    }

    public String getIdLocation() {
        return idLocation;
    }

    public String getIdUsager() {
        return idUsager;
    }

    public long getMontantCentimes() {
        return montantCentimes;
    }

    public String getStatut() {
        return statut;
    }

    public String getReferencePaiement() {
        return referencePaiement;
    }
}
