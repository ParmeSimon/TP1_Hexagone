package fr.greenmove.facturation.domaine.modele;

import fr.greenmove.facturation.domaine.exception.FactureDejaPayee;
import fr.greenmove.facturation.domaine.tarif.GrilleTarifaire;
import java.util.Objects;
import java.util.Optional;

/**
 * L'agrégat Facture : la facture d'une location terminée. Il porte ses règles, sans setter.
 *
 * <ul>
 *   <li><b>Une facture par location</b> : la facture est rattachée à une seule location, par son identifiant
 *       ({@link IdLocation}), jamais par une relation vers l'agrégat Location. L'unicité entre factures est garantie
 *       par le cas d'usage, qui cherche d'abord la facture existante de la location dans le dépôt.</li>
 *   <li><b>Statut PAYEE ou IMPAYEE</b>, et une invariante qui les lie à la référence de paiement : une facture
 *       payée a toujours une référence, une facture impayée n'en a jamais.</li>
 *   <li><b>Régularisation seulement si impayée</b> : {@link #regulariser(ReferencePaiement)} sur une facture payée
 *       lève {@link FactureDejaPayee}.</li>
 * </ul>
 *
 * <p>Le montant est reçu déjà calculé (par la {@link GrilleTarifaire}) : la facture ne connaît pas les tarifs.</p>
 */
public final class Facture {

    private final IdFacture id;
    private final IdLocation idLocation;
    private final IdUsager idUsager;
    private final Montant montant;
    private StatutFacture statut;
    private ReferencePaiement referencePaiement; // null tant que la facture est impayée

    private Facture(IdFacture id, IdLocation idLocation, IdUsager idUsager, Montant montant,
                    StatutFacture statut, ReferencePaiement referencePaiement) {
        this.id = Objects.requireNonNull(id, "id");
        this.idLocation = Objects.requireNonNull(idLocation, "idLocation");
        this.idUsager = Objects.requireNonNull(idUsager, "idUsager");
        this.montant = Objects.requireNonNull(montant, "montant");
        this.statut = Objects.requireNonNull(statut, "statut");
        if (statut == StatutFacture.PAYEE && referencePaiement == null) {
            throw new IllegalArgumentException("Une facture payée doit avoir une référence de paiement");
        }
        if (statut == StatutFacture.IMPAYEE && referencePaiement != null) {
            throw new IllegalArgumentException("Une facture impayée n'a pas de référence de paiement");
        }
        this.referencePaiement = referencePaiement;
    }

    /** Émet la facture d'une location dont le débit a été accepté par le prestataire. */
    public static Facture payee(IdFacture id, IdLocation idLocation, IdUsager idUsager, Montant montant,
                                ReferencePaiement reference) {
        return new Facture(id, idLocation, idUsager, montant, StatutFacture.PAYEE,
                Objects.requireNonNull(reference, "reference"));
    }

    /** Émet la facture d'une location dont le débit a été refusé : une décision métier, pas une erreur. */
    public static Facture impayee(IdFacture id, IdLocation idLocation, IdUsager idUsager, Montant montant) {
        return new Facture(id, idLocation, idUsager, montant, StatutFacture.IMPAYEE, null);
    }

    /**
     * Reconstitue une facture déjà émise (utilisé par l'adaptateur du dépôt). Les invariants sont revérifiés :
     * même la base ne peut pas fabriquer une facture payée sans référence.
     */
    public static Facture reconstituer(IdFacture id, IdLocation idLocation, IdUsager idUsager, Montant montant,
                                       StatutFacture statut, ReferencePaiement referencePaiement) {
        return new Facture(id, idLocation, idUsager, montant, statut, referencePaiement);
    }

    /**
     * Régularise la facture après un nouveau débit réussi.
     *
     * @throws FactureDejaPayee si la facture est déjà payée
     */
    public void regulariser(ReferencePaiement reference) {
        Objects.requireNonNull(reference, "reference");
        if (statut == StatutFacture.PAYEE) {
            throw new FactureDejaPayee(id);
        }
        this.statut = StatutFacture.PAYEE;
        this.referencePaiement = reference;
    }

    public boolean estImpayee() {
        return statut == StatutFacture.IMPAYEE;
    }

    public IdFacture id() {
        return id;
    }

    public IdLocation idLocation() {
        return idLocation;
    }

    public IdUsager idUsager() {
        return idUsager;
    }

    public Montant montant() {
        return montant;
    }

    public StatutFacture statut() {
        return statut;
    }

    /** La référence du débit, vide tant que la facture est impayée. */
    public Optional<ReferencePaiement> referencePaiement() {
        return Optional.ofNullable(referencePaiement);
    }

    /** Un agrégat a une identité : deux factures sont la même si elles ont le même identifiant. */
    @Override
    public boolean equals(Object o) {
        return o instanceof Facture autre && id.equals(autre.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Facture[" + id.valeur() + ", location " + idLocation.valeur() + ", " + montant + ", " + statut + "]";
    }
}
