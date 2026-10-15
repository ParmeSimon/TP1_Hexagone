package fr.greenmove.facturation.domaine;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Les ports sortants ne dépendent d'aucune technique : on les implémente en quelques lignes, sans Spring ni base.
 * Ce sont ces mêmes doubles écrits à la main que les tests du cas d'usage utiliseront.
 */
class PortsSortantsTest {

    private static final IdUsager U1 = new IdUsager("u1");
    private static final IdUsager U3 = new IdUsager("u3");

    @Test
    void un_paiement_accepte_porte_sa_reference() {
        ReferencePaiement ref = new ReferencePaiement("SIMU-1");
        ResultatPaiement resultat = ResultatPaiement.accepte(ref);

        assertTrue(resultat.estAccepte());
        assertEquals(ref, ((ResultatPaiement.Accepte) resultat).reference());
        assertThrows(NullPointerException.class, () -> ResultatPaiement.accepte(null));
    }

    @Test
    void un_paiement_refuse_est_une_reponse_pas_une_exception() {
        Paiement banqueQuiRefuse = (usager, montant) -> ResultatPaiement.refuse("Prélèvement rejeté");

        ResultatPaiement resultat = banqueQuiRefuse.debiter(U3, Montant.enCentimes(80));

        assertFalse(resultat.estAccepte());
        assertEquals("Prélèvement rejeté", ((ResultatPaiement.Refuse) resultat).motif());
    }

    @Test
    void abonnements_en_une_lambda() {
        Set<IdUsager> abonnes = Set.of(new IdUsager("u2"), U3);
        Abonnements abonnements = abonnes::contains;

        assertTrue(abonnements.estAbonne(U3));
        assertFalse(abonnements.estAbonne(U1));
    }

    @Test
    void le_depot_retrouve_une_facture_par_location_et_les_impayes() {
        Factures factures = new FacturesDansUneMap();
        IdFacture id = factures.prochainIdentifiant();
        Facture impayee = Facture.impayee(id, new IdLocation("L-0002"), U3, Montant.enCentimes(80));

        factures.enregistrer(impayee);

        assertEquals(new IdFacture("F-0001"), id);
        assertEquals(Optional.of(impayee), factures.parLocation(new IdLocation("L-0002")));
        assertEquals(Optional.empty(), factures.parLocation(new IdLocation("L-9999")));
        assertTrue(factures.existeUneImpayeePour(U3));
        assertFalse(factures.existeUneImpayeePour(U1));

        impayee.regulariser(new ReferencePaiement("SIMU-2"));
        factures.enregistrer(impayee);
        assertFalse(factures.existeUneImpayeePour(U3));
    }

    /** Un double du dépôt : une Map, rien de plus. */
    static class FacturesDansUneMap implements Factures {
        private final Map<IdFacture, Facture> parId = new HashMap<>();
        private int compteur;

        @Override
        public void enregistrer(Facture facture) {
            parId.put(facture.id(), facture);
        }

        @Override
        public Optional<Facture> parId(IdFacture id) {
            return Optional.ofNullable(parId.get(id));
        }

        @Override
        public Optional<Facture> parLocation(IdLocation idLocation) {
            return parId.values().stream().filter(f -> f.idLocation().equals(idLocation)).findFirst();
        }

        @Override
        public boolean existeUneImpayeePour(IdUsager idUsager) {
            return parId.values().stream().anyMatch(f -> f.idUsager().equals(idUsager) && f.estImpayee());
        }

        @Override
        public IdFacture prochainIdentifiant() {
            return new IdFacture(String.format("F-%04d", ++compteur));
        }
    }
}
