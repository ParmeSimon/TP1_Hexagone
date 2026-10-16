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
    void une_facture_payee_porte_sa_reference() {
        Facture facture = Facture.payee(ID, LOCATION, USAGER, MONTANT, REF);

        assertEquals(StatutFacture.PAYEE, facture.statut());
        assertEquals(Optional.of(REF), facture.referencePaiement());
        assertFalse(facture.estImpayee());
        assertEquals(LOCATION, facture.idLocation());
        assertEquals(MONTANT, facture.montant());
    }

    @Test
    void une_facture_impayee_n_a_pas_de_reference() {
        Facture facture = Facture.impayee(ID, LOCATION, USAGER, MONTANT);

        assertEquals(StatutFacture.IMPAYEE, facture.statut());
        assertEquals(Optional.empty(), facture.referencePaiement());
        assertTrue(facture.estImpayee());
    }

    @Test
    void une_facture_payee_sans_reference_n_existe_pas() {
        assertThrows(NullPointerException.class, () -> Facture.payee(ID, LOCATION, USAGER, MONTANT, null));
        assertThrows(IllegalArgumentException.class,
                () -> Facture.reconstituer(ID, LOCATION, USAGER, MONTANT, StatutFacture.PAYEE, null));
        assertThrows(IllegalArgumentException.class,
                () -> Facture.reconstituer(ID, LOCATION, USAGER, MONTANT, StatutFacture.IMPAYEE, REF));
    }

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

    @Test
    void deux_factures_de_meme_identifiant_sont_la_meme_facture() {
        Facture avant = Facture.impayee(ID, LOCATION, USAGER, MONTANT);
        Facture apres = Facture.payee(ID, LOCATION, USAGER, MONTANT, REF);
        assertEquals(avant, apres);
    }

    @Test
    void les_identifiants_vides_sont_refuses() {
        assertThrows(IllegalArgumentException.class, () -> new IdFacture(" "));
        assertThrows(IllegalArgumentException.class, () -> new IdLocation(""));
        assertThrows(IllegalArgumentException.class, () -> new IdUsager(null));
        assertThrows(IllegalArgumentException.class, () -> new ReferencePaiement(""));
    }
}
