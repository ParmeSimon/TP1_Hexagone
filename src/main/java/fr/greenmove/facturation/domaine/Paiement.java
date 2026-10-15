package fr.greenmove.facturation.domaine;

/**
 * Port sortant : débiter un usager chez le prestataire de paiement.
 *
 * <p>Un refus n'est <b>pas</b> une exception : c'est une réponse normale du prestataire, que le domaine transforme
 * en facture {@code IMPAYEE}. L'adaptateur (par-dessus {@code PaiementSimule}) attrape l'exception technique
 * {@code DebitRefuse} et renvoie {@link ResultatPaiement.Refuse}. Quand l'API du prestataire change (une à deux
 * fois par an), seul l'adaptateur change, pas ce port.</p>
 */
public interface Paiement {

    /** Tente de débiter l'usager du montant : accepté avec une référence, ou refusé avec un motif. */
    ResultatPaiement debiter(IdUsager idUsager, Montant montant);
}
