package fr.greenmove.facturation.domaine.tarif;

import fr.greenmove.facturation.domaine.modele.Facture;
import fr.greenmove.facturation.domaine.modele.Montant;
import fr.greenmove.facturation.domaine.modele.TypeVehicule;

/**
 * La grille tarifaire : combien coûte une location. Séparée de {@link Facture} et <b>remplaçable</b>.
 *
 * <p>La facture ne sait pas calculer un prix : elle reçoit un {@link Montant} déjà calculé. Le marketing change la
 * grille tous les six mois : on écrit une autre implémentation (ou une autre instance de {@link GrilleTarifaireM01}
 * avec d'autres valeurs) et on la passe au cas d'usage, sans toucher à {@code Facture}.</p>
 */
@FunctionalInterface
public interface GrilleTarifaire {

    /**
     * Le montant à facturer.
     *
     * @param type         le type de véhicule loué
     * @param dureeMinutes la durée en minutes entières (déjà arrondie à la minute inférieure), jamais négative
     * @param abonne       vrai si l'usager a un abonnement
     */
    Montant tarifer(TypeVehicule type, long dureeMinutes, boolean abonne);

    /** La grille en vigueur : celle du M01. */
    static GrilleTarifaire standard() {
        return GrilleTarifaireM01.standard();
    }
}
