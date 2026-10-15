package fr.greenmove.location.domaine;

import fr.greenmove.location.domaine.exceptions.*;
import fr.greenmove.location.domaine.modele.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class LocationTest {

    private static final Instant T0 = Instant.parse("2026-10-20T13:40:00Z");
    private static final Vehicule VELO_DISPO =
            new Vehicule(new IdVehicule("V428"), TypeVehicule.VELO, true);

    private Location nouvelle() {
        return Location.demarrer(new IdLocation("L-0001"), new IdUsager("u1"), VELO_DISPO, T0);
    }

    @Test
    void une_location_demarree_est_en_cours() {
        Location l = nouvelle();
        assertEquals(Statut.EN_COURS, l.statut());
        assertNull(l.dureeMinutes());
    }

    @Test
    void on_ne_demarre_pas_sur_un_vehicule_indisponible() {
        Vehicule enPanne = new Vehicule(new IdVehicule("V502"), TypeVehicule.VELO, false);
        assertThrows(VehiculeIndisponible.class,
                () -> Location.demarrer(new IdLocation("L-1"), new IdUsager("u1"), enPanne, T0));
    }

    @Test
    void terminer_passe_la_location_en_TERMINEE() {
        Location l = nouvelle();
        l.terminer(T0.plusSeconds(60));
        assertEquals(Statut.TERMINEE, l.statut());
    }

    @Test
    void la_duree_est_arrondie_a_la_minute_inferieure() {
        Location l = nouvelle();
        l.terminer(T0.plusSeconds(179));          // 2 min 59 s
        assertEquals(2L, l.dureeMinutes());
    }

    @Test
    void zero_minute_si_moins_d_une_minute() {
        Location l = nouvelle();
        l.terminer(T0.plusSeconds(12));
        assertEquals(0L, l.dureeMinutes());
    }

    @Test
    void on_ne_termine_pas_deux_fois() {
        Location l = nouvelle();
        l.terminer(T0.plusSeconds(60));
        assertThrows(LocationDejaTerminee.class, () -> l.terminer(T0.plusSeconds(120)));
    }

    @Test
    void la_fin_ne_peut_pas_preceder_le_debut() {
        Location l = nouvelle();
        assertThrows(FinAvantDebut.class, () -> l.terminer(T0.minusSeconds(1)));
    }
}