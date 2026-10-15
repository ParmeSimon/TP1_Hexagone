package fr.greenmove.facturation.domaine;

/**
 * Exception métier : la régularisation a échoué, le prestataire refuse encore le débit (l'API la traduira en 502).
 *
 * <p>Pourquoi une exception ici, alors qu'un refus à l'émission n'en lève pas ? À l'émission, le refus <b>est</b> le
 * résultat attendu : on enregistre une facture IMPAYEE et c'est fini. À la régularisation, l'appelant a demandé
 * « faites payer cette facture » et ça n'a pas pu se faire : la demande a échoué, l'appelant doit le savoir. La
 * facture, elle, reste IMPAYEE, sans aucune modification.</p>
 */
public class PaiementEncoreRefuse extends RuntimeException {

    private final IdFacture idFacture;

    public PaiementEncoreRefuse(IdFacture idFacture, String motif) {
        super("Paiement encore refusé pour la facture " + idFacture.valeur() + " : " + motif);
        this.idFacture = idFacture;
    }

    public IdFacture idFacture() {
        return idFacture;
    }
}
