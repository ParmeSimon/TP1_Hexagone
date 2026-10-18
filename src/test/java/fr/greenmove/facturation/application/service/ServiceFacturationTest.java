package fr.greenmove.facturation.application.service;

import fr.greenmove.facturation.application.EmettreFacture;
import fr.greenmove.facturation.application.FactureVue;
import fr.greenmove.facturation.domaine.port.sortant.Abonnements;
import fr.greenmove.facturation.domaine.modele.Facture;
import fr.greenmove.facturation.domaine.port.sortant.Factures;
import fr.greenmove.facturation.domaine.tarif.GrilleTarifaire;
import fr.greenmove.facturation.domaine.modele.IdFacture;
import fr.greenmove.facturation.domaine.modele.IdLocation;
import fr.greenmove.facturation.domaine.modele.IdUsager;
import fr.greenmove.facturation.domaine.modele.Montant;
import fr.greenmove.facturation.domaine.port.sortant.Paiement;
import fr.greenmove.facturation.domaine.modele.ReferencePaiement;
import fr.greenmove.facturation.domaine.port.sortant.ResultatPaiement;
import fr.greenmove.facturation.domaine.modele.TypeVehicule;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Le cas d'usage ServiceFacturation, avec des doubles écrits à la main : une Map pour le dépôt, un prestataire de
 * paiement programmable, un ensemble d'abonnés. Ni Spring, ni base, ni PaiementSimule.
 */
class ServiceFacturationTest {

    private static final Instant DEBUT = Instant.parse("2026-10-20T13:40:00Z");
    private static final IdUsager U1 = new IdUsager("u1");   // non abonné
    private static final IdUsager U2 = new IdUsager("u2");   // abonné
    private static final IdUsager U3 = new IdUsager("u3");   // abonné, prélèvement rejeté

    private final FacturesDansUneMap factures = new FacturesDansUneMap();
    private final PaiementProgrammable paiement = new PaiementProgrammable();
    private final Abonnements abonnements = Set.of(U2, U3)::contains;

    private final ServiceFacturation service =
            new ServiceFacturation(factures, paiement, abonnements, GrilleTarifaire.standard());

    // ------------------------------------------------------------------ émettre

    @Test
    void velo_non_abonne_24_minutes_facture_payee_avec_sa_reference() {
        paiement.accepteraAvec("SIMU-AAA");

        FactureVue facture = service.emettre(commande("L-0001", U1, TypeVehicule.VELO, 24));

        assertEquals(460, facture.montantCentimes());
        assertEquals("PAYEE", facture.statut());
        assertEquals("SIMU-AAA", facture.referencePaiement());
        assertEquals("L-0001", facture.location());
        assertEquals("u1", facture.usager());
        assertEquals("F-0001", facture.id());
        assertEquals(1, paiement.debits);
        assertEquals(460, paiement.dernierMontant.centimes());
    }

    @Test
    void un_debit_refuse_donne_une_facture_impayee_sans_exception() {
        paiement.refusera();

        FactureVue facture = assertDoesNotThrow(
                () -> service.emettre(commande("L-0002", U3, TypeVehicule.TROTTINETTE, 0)));

        assertEquals("IMPAYEE", facture.statut());
        assertNull(facture.referencePaiement());
        assertEquals(80, facture.montantCentimes());
        assertEquals(Optional.of(facture), service.parLocation(new IdLocation("L-0002"))); // bien enregistrée
    }

    @Test
    void emettre_deux_fois_la_meme_location_renvoie_la_meme_facture_sans_second_debit() {
        paiement.accepteraAvec("SIMU-1", "SIMU-2");

        FactureVue premiere = service.emettre(commande("L-0001", U1, TypeVehicule.VELO, 24));
        FactureVue seconde = service.emettre(commande("L-0001", U1, TypeVehicule.VELO, 24));

        assertEquals(premiere, seconde);
        assertEquals(1, paiement.debits);
        assertEquals(1, factures.taille());
    }

    // ------------------------------------------------------------------ régulariser

    @Test
    void regulariser_une_facture_impayee_la_rend_payee_et_debloque_l_usager() {
        paiement.refusera();
        FactureVue impayee = service.emettre(commande("L-0002", U3, TypeVehicule.TROTTINETTE, 0));
        paiement.accepteraAvec("SIMU-8C41D0E7");

        FactureVue payee = service.regulariser(new IdFacture(impayee.id()));

        assertEquals("PAYEE", payee.statut());
        assertEquals("SIMU-8C41D0E7", payee.referencePaiement());
        assertEquals(impayee.id(), payee.id());
        assertEquals(80, paiement.dernierMontant.centimes()); // on redébite le montant de la facture
        assertFalse(service.aUneFactureImpayee("u3"));
    }

    // ------------------------------------------------------------------ outils

    private static EmettreFacture.Commande commande(String location, IdUsager usager, TypeVehicule type,
                                                    long minutes) {
        return new EmettreFacture.Commande(new IdLocation(location), usager, type, DEBUT,
                DEBUT.plus(Duration.ofMinutes(minutes)));
    }

    /** Double du port Factures : une Map, et des identifiants F-0001, F-0002... */
    static class FacturesDansUneMap implements Factures {
        private final Map<IdFacture, Facture> parId = new LinkedHashMap<>();
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

        int taille() {
            return parId.size();
        }
    }

    /** Double du port Paiement : on lui dit à l'avance ce qu'il répondra, il compte les débits. */
    static class PaiementProgrammable implements Paiement {
        private final Deque<ResultatPaiement> reponses = new ArrayDeque<>();
        int debits;
        Montant dernierMontant;

        void accepteraAvec(String... references) {
            for (String reference : references) {
                reponses.add(ResultatPaiement.accepte(new ReferencePaiement(reference)));
            }
        }

        void refusera() {
            reponses.add(ResultatPaiement.refuse("Prélèvement rejeté"));
        }

        @Override
        public ResultatPaiement debiter(IdUsager idUsager, Montant montant) {
            debits++;
            dernierMontant = montant;
            ResultatPaiement reponse = reponses.poll();
            if (reponse == null) {
                throw new AssertionError("Débit inattendu de " + montant + " pour " + idUsager.valeur());
            }
            return reponse;
        }
    }
}
