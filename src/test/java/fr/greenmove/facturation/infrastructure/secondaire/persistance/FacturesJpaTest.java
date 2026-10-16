package fr.greenmove.facturation.infrastructure.secondaire.persistance;

import fr.greenmove.facturation.domaine.modele.Facture;
import fr.greenmove.facturation.domaine.port.sortant.Factures;
import fr.greenmove.facturation.domaine.modele.IdFacture;
import fr.greenmove.facturation.domaine.modele.IdLocation;
import fr.greenmove.facturation.domaine.modele.IdUsager;
import fr.greenmove.facturation.domaine.modele.Montant;
import fr.greenmove.facturation.domaine.modele.ReferencePaiement;
import fr.greenmove.facturation.domaine.modele.StatutFacture;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/**
 * L'adaptateur JPA contre une vraie base H2. {@code @DataJpaTest} ne démarre que JPA (pas le web, pas les autres
 * beans) ; l'adaptateur est construit à la main avec le repository, comme dans la configuration.
 */
@DataJpaTest
class FacturesJpaTest {

    private static final IdUsager U3 = new IdUsager("u3");

    @Autowired
    FactureJpaRepository jpa;

    Factures factures;

    @BeforeEach
    void setUp() {
        factures = new FacturesJpa(jpa);
    }

    @Test
    void une_facture_enregistree_se_relit_a_l_identique() {
        Facture payee = Facture.payee(factures.prochainIdentifiant(), new IdLocation("L-0001"), new IdUsager("u1"),
                Montant.enCentimes(460), new ReferencePaiement("SIMU-AAA"));

        factures.enregistrer(payee);

        Facture relue = factures.parId(payee.id()).orElseThrow();
        assertEquals(payee.id(), relue.id());
        assertEquals(new IdLocation("L-0001"), relue.idLocation());
        assertEquals(new IdUsager("u1"), relue.idUsager());
        assertEquals(Montant.enCentimes(460), relue.montant());
        assertEquals(StatutFacture.PAYEE, relue.statut());
        assertEquals(Optional.of(new ReferencePaiement("SIMU-AAA")), relue.referencePaiement());
    }

    @Test
    void retrouver_par_location_et_detecter_les_impayes() {
        Facture impayee = Facture.impayee(factures.prochainIdentifiant(), new IdLocation("L-0002"), U3,
                Montant.enCentimes(80));
        factures.enregistrer(impayee);

        assertEquals(Optional.of(impayee), factures.parLocation(new IdLocation("L-0002")));
        assertTrue(factures.parLocation(new IdLocation("L-9999")).isEmpty());
        assertTrue(factures.existeUneImpayeePour(U3));
        assertFalse(factures.existeUneImpayeePour(new IdUsager("u1")));
    }

    @Test
    void une_regularisation_enregistree_met_a_jour_la_ligne() {
        Facture facture = Facture.impayee(factures.prochainIdentifiant(), new IdLocation("L-0002"), U3,
                Montant.enCentimes(80));
        factures.enregistrer(facture);

        facture.regulariser(new ReferencePaiement("SIMU-8C41D0E7"));
        factures.enregistrer(facture);

        assertEquals(1, jpa.count());
        assertEquals(StatutFacture.PAYEE, factures.parId(facture.id()).orElseThrow().statut());
        assertFalse(factures.existeUneImpayeePour(U3));
    }

    @Test
    void les_identifiants_se_suivent() {
        IdFacture premier = factures.prochainIdentifiant();
        IdFacture second = factures.prochainIdentifiant();

        assertEquals(new IdFacture("F-0001"), premier);
        assertEquals(new IdFacture("F-0002"), second);
    }
}
