package fr.greenmove.location.infrastructure.flotte;

import fr.greenmove.exterieur.FlotteEnMemoire;
import fr.greenmove.location.domaine.Flotte;
import fr.greenmove.location.domaine.IdVehicule;
import fr.greenmove.location.domaine.TypeVehicule;
import fr.greenmove.location.domaine.Vehicule;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Le test d'un adaptateur, fourni en exemple : on construit l'adaptateur à la main avec le vrai système extérieur
 * (ici la simulation), sans Spring. Il tourne en quelques millisecondes.
 */
class FlotteSurSimulationTest {

    private final Flotte flotte = new FlotteSurSimulation(new FlotteEnMemoire());

    @Test
    void traduit_une_entree_du_csv_en_vehicule_du_domaine() {
        Vehicule v = flotte.vehicule(new IdVehicule("V428")).orElseThrow();

        assertEquals(TypeVehicule.VELO, v.type());
        assertTrue(v.disponible());
    }

    @Test
    void un_vehicule_en_maintenance_est_indisponible() {
        assertFalse(flotte.vehicule(new IdVehicule("V502")).orElseThrow().disponible());
    }

    @Test
    void un_vehicule_inconnu_donne_vide() {
        assertTrue(flotte.vehicule(new IdVehicule("X999")).isEmpty());
    }
}
