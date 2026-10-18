package fr.greenmove.facturation.domaine.exception;

import fr.greenmove.facturation.domaine.modele.IdFacture;

/** Exception métier : on ne régularise pas une facture déjà payée (l'API la traduira en 409). */
public class FactureDejaPayee extends RuntimeException {

    private final IdFacture idFacture;

    public FactureDejaPayee(IdFacture idFacture) {
        super("La facture " + idFacture.valeur() + " est déjà payée");
        this.idFacture = idFacture;
    }

    public IdFacture idFacture() {
        return idFacture;
    }
}
