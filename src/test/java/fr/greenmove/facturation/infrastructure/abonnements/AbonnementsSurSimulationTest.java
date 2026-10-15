package fr.greenmove.facturation.infrastructure.abonnements;

import fr.greenmove.exterieur.UsagersEnMemoire;
import fr.greenmove.facturation.domaine.Abonnements;
import fr.greenmove.facturation.domaine.IdUsager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/** L'adaptateur Abonnements, construit à la main avec les données de data/usagers.csv, sans Spring. */
class AbonnementsSurSimulationTest {

    private final Abonnements abonnements = new AbonnementsSurSimulation(new UsagersEnMemoire());

    @Test
    void u2_et_u3_sont_abonnes() {
        assertTrue(abonnements.estAbonne(new IdUsager("u2")));
        assertTrue(abonnements.estAbonne(new IdUsager("u3")));
    }

    @Test
    void u1_n_est_pas_abonne() {
        assertFalse(abonnements.estAbonne(new IdUsager("u1")));
    }

    @Test
    void un_usager_inconnu_n_est_pas_abonne() {
        assertFalse(abonnements.estAbonne(new IdUsager("inconnu")));
    }
}
