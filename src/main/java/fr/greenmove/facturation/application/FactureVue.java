package fr.greenmove.facturation.application;

import fr.greenmove.facturation.domaine.Facture;
import fr.greenmove.facturation.domaine.ReferencePaiement;

/**
 * Ce que les ports entrants renvoient : une photo de la facture, en types simples, qui correspond au corps de
 * réponse de l'annexe A.
 *
 * <p>On ne renvoie pas l'agrégat {@link Facture} lui-même : l'appelant (le contrôleur REST, demain un autre
 * adaptateur) pourrait sinon appeler {@code regulariser(...)} dessus sans passer par le cas d'usage.</p>
 *
 * @param referencePaiement {@code null} tant que la facture est impayée
 */
public record FactureVue(String id, String location, String usager, long montantCentimes, String statut,
                         String referencePaiement) {

    public static FactureVue de(Facture facture) {
        return new FactureVue(
                facture.id().valeur(),
                facture.idLocation().valeur(),
                facture.idUsager().valeur(),
                facture.montant().centimes(),
                facture.statut().name(),
                facture.referencePaiement().map(ReferencePaiement::valeur).orElse(null));
    }
}
