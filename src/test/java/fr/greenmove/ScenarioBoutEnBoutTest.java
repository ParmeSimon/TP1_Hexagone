package fr.greenmove;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Le scénario de l'annexe C de l'énoncé, joué à travers l'API : les deux hexagones et leur communication.
 *
 * <p>Désactivé dans le code de départ. Retirez {@code @Disabled} quand les deux hexagones sont câblés : s'il passe,
 * l'événement traverse le bus, l'appel synchrone traverse le port, et l'échec du paiement est traité.
 * Il ne contrôle pas le temps : les locations durent 0 minute, donc 100 centimes (80 pour un abonné).
 * Les durées se testent dans le domaine, avec votre port Horloge.</p>
 */
@SpringBootTest
@AutoConfigureMockMvc
@Disabled("À activer quand les deux hexagones sont câblés (annexe C de l'énoncé)")
class ScenarioBoutEnBoutTest {

    @Autowired
    MockMvc mvc;

    @Test
    void u1_loue_un_velo_termine_et_sa_facture_est_payee() throws Exception {
        String idLocation = demarrer("u1", "V428");

        mvc.perform(post("/locations/" + idLocation + "/fin"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("TERMINEE"));

        mvc.perform(get("/factures").param("location", idLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usager").value("u1"))
                .andExpect(jsonPath("$.montantCentimes").value(100))
                .andExpect(jsonPath("$.statut").value("PAYEE"));
    }

    @Test
    void u3_a_un_prelevement_rejete_ne_peut_plus_louer_puis_regularise() throws Exception {
        String idLocation = demarrer("u3", "T117");
        mvc.perform(post("/locations/" + idLocation + "/fin")).andExpect(status().isOk());

        // la location est terminée quand même : la facture est impayée, Location n'a pas vu l'échec
        String facture = mvc.perform(get("/factures").param("location", idLocation))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.montantCentimes").value(80))   // abonné : 100 moins 20 %
                .andExpect(jsonPath("$.statut").value("IMPAYEE"))
                .andReturn().getResponse().getContentAsString();
        String idFacture = JsonPath.read(facture, "$.id");

        // l'appel synchrone : Location demande à Facturation, qui dit non
        mvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usager\":\"u3\",\"vehicule\":\"T118\"}"))
                .andExpect(status().isConflict());

        // la régularisation : le second prélèvement passe
        mvc.perform(post("/factures/" + idFacture + "/regularisation"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.statut").value("PAYEE"));

        demarrer("u3", "T118");
    }

    private String demarrer(String usager, String vehicule) throws Exception {
        String corps = mvc.perform(post("/locations").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usager\":\"" + usager + "\",\"vehicule\":\"" + vehicule + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.statut").value("EN_COURS"))
                .andReturn().getResponse().getContentAsString();
        return JsonPath.read(corps, "$.id");
    }
}
