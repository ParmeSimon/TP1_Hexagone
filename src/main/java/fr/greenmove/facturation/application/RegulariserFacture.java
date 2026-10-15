package fr.greenmove.facturation.application;

import fr.greenmove.facturation.domaine.FactureDejaPayee;
import fr.greenmove.facturation.domaine.FactureInconnue;
import fr.greenmove.facturation.domaine.IdFacture;
import fr.greenmove.facturation.domaine.PaiementEncoreRefuse;

/** Port entrant : nouvelle tentative de débit pour une facture impayée. */
public interface RegulariserFacture {

    /**
     * Retente le débit ; si le prestataire accepte, la facture passe {@code PAYEE} avec la nouvelle référence.
     *
     * @throws FactureInconnue      si aucune facture ne porte cet identifiant (404)
     * @throws FactureDejaPayee     si la facture est déjà payée : aucun débit n'est tenté (409)
     * @throws PaiementEncoreRefuse si le prestataire refuse encore : la facture reste {@code IMPAYEE} (502)
     */
    FactureVue regulariser(IdFacture idFacture);
}
