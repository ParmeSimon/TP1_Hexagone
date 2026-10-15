package fr.greenmove.facturation.application;

import fr.greenmove.facturation.domaine.IdLocation;
import fr.greenmove.facturation.domaine.IdUsager;
import fr.greenmove.facturation.domaine.TypeVehicule;

import java.time.Duration;
import java.time.Instant;
import java.util.Objects;

/**
 * Port entrant : émettre la facture d'une location terminée.
 *
 * <p>Appelé par l'adaptateur primaire qui écoute {@code LocationTerminee} sur le bus : il traduit l'événement (des
 * chaînes) en {@link Commande} (les types de Facturation) et appelle ce port.</p>
 *
 * <ul>
 *   <li>Calcule le montant selon la grille et l'abonnement, débite chez le prestataire, enregistre la facture
 *       {@code PAYEE} avec sa référence ou {@code IMPAYEE} si le débit est refusé.</li>
 *   <li>Un refus de paiement ne lève <b>aucune</b> exception : c'est une décision métier, pas une erreur.</li>
 *   <li>Idempotent : émettre deux fois pour la même location renvoie la même facture, sans second débit.</li>
 * </ul>
 */
public interface EmettreFacture {

    FactureVue emettre(Commande commande);

    /** Les faits d'une location terminée, dans les types de Facturation. */
    record Commande(IdLocation idLocation, IdUsager idUsager, TypeVehicule typeVehicule, Instant debut, Instant fin) {

        public Commande {
            Objects.requireNonNull(idLocation, "idLocation");
            Objects.requireNonNull(idUsager, "idUsager");
            Objects.requireNonNull(typeVehicule, "typeVehicule");
            Objects.requireNonNull(debut, "debut");
            Objects.requireNonNull(fin, "fin");
            if (fin.isBefore(debut)) {
                throw new IllegalArgumentException("Fin antérieure au début pour la location " + idLocation.valeur());
            }
        }

        /** La durée facturée, en minutes entières arrondies à la minute inférieure (comme Location). */
        public long dureeMinutes() {
            return Duration.between(debut, fin).toMinutes();
        }
    }
}
