package fr.greenmove.facturation.infrastructure.rest;

import fr.greenmove.exterieur.PaiementSimule;
import fr.greenmove.exterieur.UsagersEnMemoire;
import fr.greenmove.facturation.application.EmettreFacture;
import fr.greenmove.facturation.application.ServiceFacturation;
import fr.greenmove.facturation.domaine.Facture;
import fr.greenmove.facturation.domaine.Factures;
import fr.greenmove.facturation.domaine.GrilleTarifaire;
import fr.greenmove.facturation.domaine.IdFacture;
import fr.greenmove.facturation.domaine.IdLocation;
import fr.greenmove.facturation.domaine.IdUsager;
import fr.greenmove.facturation.domaine.Paiement;
import fr.greenmove.facturation.domaine.ResultatPaiement;
import fr.greenmove.facturation.domaine.TypeVehicule;
import fr.greenmove.facturation.infrastructure.abonnements.AbonnementsSurSimulation;
import fr.greenmove.facturation.infrastructure.paiement.PaiementSurSimulation;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Le contrôleur contre le contrat de l'annexe A. MockMvc en mode autonome ({@code standaloneSetup}) : pas de contexte
 * Spring, pas de base ; le contrôleur reçoit un vrai {@link ServiceFacturation} branché sur les simulations et un
 * dépôt en mémoire.
 */
class FactureControllerTest {

    private static final Instant DEBUT = Instant.parse("2026-10-20T13:40:00Z");

    private final ServiceFacturation service = new ServiceFacturation(new FacturesEnMemoire(),
            new PaiementSurSimulation(new PaiementSimule()), new AbonnementsSurSimulation(new UsagersEnMemoire()),
            GrilleTarifaire.standard());

    private final MockMvc mvc = MockMvcBuilders.standaloneSetup(new FactureController(service, service)).build();

    @Test
    void facture_d_une_location_payee() throws Exception {
        emettre(service, "L-0001", "u1", TypeVehicule.VELO);

        mvc.perform(get("/factures").param("location", "L-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("F-0001"))
                .andExpect(jsonPath("$.location").value("L-0001"))
                .andExpect(jsonPath("$.usager").value("u1"))
                .andExpect(jsonPath("$.montantCentimes").value(100))
                .andExpect(jsonPath("$.statut").value("PAYEE"))
                .andExpect(jsonPath("$.referencePaiement").value(startsWith("SIMU-")));
    }

    @Test
    void pas_de_facture_pour_cette_location_donne_404() throws Exception {
        mvc.perform(get("/factures").param("location", "L-9999")).andExpect(status().isNotFound());
    }

    @Test
    void consulter_une_facture_par_son_id() throws Exception {
        emettre(service, "L-0001", "u1", TypeVehicule.VELO);

        mvc.perform(get("/factures/F-0001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.location").value("L-0001"));
        mvc.perform(get("/factures/F-9999")).andExpect(status().isNotFound());
    }

    @Test
    void u3_facture_impayee_puis_regularisee_puis_deja_payee() throws Exception {
        emettre(service, "L-0002", "u3", TypeVehicule.TROTTINETTE);

        mvc.perform(get("/factures").param("location", "L-0002"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantCentimes").value(80))
                .andExpect(jsonPath("$.statut").value("IMPAYEE"))
                .andExpect(jsonPath("$.referencePaiement").value(nullValue()));

        mvc.perform(post("/factures/F-0001/regularisation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value("F-0001"))
                .andExpect(jsonPath("$.statut").value("PAYEE"))
                .andExpect(jsonPath("$.referencePaiement").value(startsWith("SIMU-")));

        mvc.perform(post("/factures/F-0001/regularisation")).andExpect(status().isConflict());
    }

    @Test
    void regulariser_une_facture_inconnue_donne_404() throws Exception {
        mvc.perform(post("/factures/F-9999/regularisation")).andExpect(status().isNotFound());
    }

    @Test
    void paiement_encore_refuse_donne_502() throws Exception {
        Paiement refuseToujours = (usager, montant) -> ResultatPaiement.refuse("Prélèvement rejeté");
        ServiceFacturation serviceQuiRefuse = new ServiceFacturation(new FacturesEnMemoire(), refuseToujours,
                usager -> false, GrilleTarifaire.standard());
        MockMvc mvcQuiRefuse = MockMvcBuilders
                .standaloneSetup(new FactureController(serviceQuiRefuse, serviceQuiRefuse)).build();
        emettre(serviceQuiRefuse, "L-0002", "u3", TypeVehicule.TROTTINETTE);

        mvcQuiRefuse.perform(post("/factures/F-0001/regularisation")).andExpect(status().isBadGateway());
        mvcQuiRefuse.perform(get("/factures/F-0001")).andExpect(jsonPath("$.statut").value("IMPAYEE"));
    }

    // ------------------------------------------------------------------ outils

    /** Une location de 0 minute, comme dans l'annexe C : 100 centimes, 80 pour un abonné. */
    private static void emettre(ServiceFacturation service, String location, String usager, TypeVehicule type) {
        service.emettre(new EmettreFacture.Commande(new IdLocation(location), new IdUsager(usager), type, DEBUT, DEBUT));
    }

    /** Un dépôt en mémoire pour ce test : une Map. */
    static class FacturesEnMemoire implements Factures {
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
    }
}
