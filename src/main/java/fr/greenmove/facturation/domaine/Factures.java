package fr.greenmove.facturation.domaine;

import java.util.Optional;

/**
 * Port sortant : le dépôt des factures, en vocabulaire métier et avec les types du domaine.
 *
 * <p>Le domaine ne sait pas où les factures sont rangées : une {@code Map} en mémoire, une table JPA
 * {@code facture}, un autre service demain. L'adaptateur (dans {@code infrastructure.persistance}) traduit entre
 * {@link Facture} et sa représentation technique.</p>
 */
public interface Factures {

    /** Enregistre une facture nouvelle ou modifiée (après une régularisation, par exemple). */
    void enregistrer(Facture facture);

    /** La facture, ou vide si l'identifiant est inconnu. */
    Optional<Facture> parId(IdFacture id);

    /**
     * La facture de cette location, ou vide si elle n'a pas encore été émise. C'est ce qui permet au cas d'usage de
     * garantir « une facture par location » : émettre deux fois renvoie la facture déjà trouvée ici.
     */
    Optional<Facture> parLocation(IdLocation idLocation);

    /** Vrai si l'usager a au moins une facture en statut {@code IMPAYEE}. */
    boolean existeUneImpayeePour(IdUsager idUsager);

    /** Un nouvel identifiant, jamais encore attribué (ex. « F-0001 », « F-0002 »...). */
    IdFacture prochainIdentifiant();
}
