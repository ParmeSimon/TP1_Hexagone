package fr.greenmove.facturation.domaine;

import java.util.Objects;

/**
 * La réponse du prestataire, en termes métier : soit le débit est accepté (avec sa référence), soit il est refusé.
 *
 * <p>{@code sealed} : il n'existe que ces deux cas, le compilateur le sait. Le cas d'usage teste
 * {@code instanceof ResultatPaiement.Accepte accepte} et récupère la référence, sans {@code try/catch}.</p>
 */
public sealed interface ResultatPaiement permits ResultatPaiement.Accepte, ResultatPaiement.Refuse {

    /** Le débit est passé : la facture sera {@code PAYEE} avec cette référence. */
    record Accepte(ReferencePaiement reference) implements ResultatPaiement {
        public Accepte {
            Objects.requireNonNull(reference, "reference");
        }
    }

    /** Le débit a été refusé (ex. prélèvement rejeté par la banque) : la facture sera {@code IMPAYEE}. */
    record Refuse(String motif) implements ResultatPaiement {
        public Refuse {
            motif = (motif == null || motif.isBlank()) ? "Débit refusé" : motif;
        }
    }

    static ResultatPaiement accepte(ReferencePaiement reference) {
        return new Accepte(reference);
    }

    static ResultatPaiement refuse(String motif) {
        return new Refuse(motif);
    }

    default boolean estAccepte() {
        return this instanceof Accepte;
    }
}
