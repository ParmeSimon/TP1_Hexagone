package fr.greenmove.facturation.domaine.exception;

import fr.greenmove.facturation.domaine.modele.Facture;
import fr.greenmove.facturation.domaine.modele.IdFacture;

/** Exception métier : aucune facture ne porte cet identifiant (l'API la traduira en 404). */
public class FactureInconnue extends RuntimeException {

    private final IdFacture idFacture;

    public FactureInconnue(IdFacture idFacture) {
        super("Facture inconnue : " + idFacture.valeur());
        this.idFacture = idFacture;
    }

    public IdFacture idFacture() {
        return idFacture;
    }
}
