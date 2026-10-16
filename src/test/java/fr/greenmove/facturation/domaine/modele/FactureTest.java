package fr.greenmove.facturation.domaine.modele;

import fr.greenmove.facturation.domaine.exception.FactureDejaPayee;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** L'agrégat Facture et ses règles. Sans Spring ni base. */
class FactureTest {

    private static final IdFacture ID = new IdFacture("F-0001");
    private static final IdLocation LOCATION = new IdLocation("L-0001");
    private static final IdUsager USAGER = new IdUsager("u1");
    private static final Montant MONTANT = Montant.enCentimes(100);
    private static final ReferencePaiement REF = new ReferencePaiement("SIMU-3F2A9C1B");

    @Test
    void regulariser_une_facture_impayee_la_rend_payee() {
        Facture facture = Facture.impayee(ID, LOCATION, USAGER, MONTANT);
        ReferencePaiement secondDebit = new ReferencePaiement("SIMU-8C41D0E7");

        facture.regulariser(secondDebit);

        assertEquals(StatutFacture.PAYEE, facture.statut());
        assertEquals(Optional.of(secondDebit), facture.referencePaiement());
    }

    @Test
    void on_ne_regularise_pas_une_facture_deja_payee() {
        Facture facture = Facture.payee(ID, LOCATION, USAGER, MONTANT, REF);

        FactureDejaPayee erreur = assertThrows(FactureDejaPayee.class,
                () -> facture.regulariser(new ReferencePaiement("SIMU-AUTRE")));

        assertEquals(ID, erreur.idFacture());
        assertEquals(Optional.of(REF), facture.referencePaiement()); // rien n'a bougé
    }

}
