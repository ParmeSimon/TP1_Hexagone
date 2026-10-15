package fr.greenmove.location.application;

import fr.greenmove.location.domaine.evenements.LocationTerminee;
import fr.greenmove.location.domaine.exceptions.*;
import fr.greenmove.location.domaine.modele.*;
import fr.greenmove.location.domaine.ports.*;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class ServiceLocationTest {

    static final Instant T0 = Instant.parse("2026-10-20T13:40:00Z");

    /** Double du dépôt : une simple Map en mémoire. */
    static class LocationsEnMemoire implements Locations {
        final Map<IdLocation, Location> stock = new HashMap<>();
        int compteur = 0;

        public IdLocation prochainId() { return new IdLocation("L-" + (++compteur)); }
        public void sauvegarder(Location l) { stock.put(l.id(), l); }
        public Optional<Location> parId(IdLocation id) { return Optional.ofNullable(stock.get(id)); }
        public boolean aUneLocationEnCours(IdUsager u) {
            return stock.values().stream().anyMatch(l -> l.usager().equals(u) && l.estEnCours());
        }
    }

    // Les autres doubles : des lambdas, car chaque port n'a qu'une méthode
    final LocationsEnMemoire locations = new LocationsEnMemoire();
    final List<LocationTerminee> publies = new ArrayList<>();
    final Flotte flotte = id -> Optional.of(new Vehicule(id, TypeVehicule.VELO, true));
    final Usagers usagers = u -> true;            // tout le monde existe
    final Horloge horlogeFixe = () -> T0;         // il est toujours T0

    ServiceLocation service(Impayes impayes) {
        return new ServiceLocation(locations, flotte, usagers, impayes, publies::add, horlogeFixe);
    }

    @Test
    void demarrer_cree_une_location_en_cours() {
        Location l = service(u -> false).demarrer(new IdUsager("u1"), new IdVehicule("V428"));

        assertEquals(Statut.EN_COURS, l.statut());
        assertEquals(T0, l.debut());
        assertTrue(locations.parId(l.id()).isPresent());   // elle a bien été sauvegardée
    }

    @Test
    void terminer_enregistre_et_publie_LocationTerminee() {
        ServiceLocation service = service(u -> false);
        Location l = service.demarrer(new IdUsager("u1"), new IdVehicule("V428"));

        service.terminer(l.id());

        assertEquals(Statut.TERMINEE, locations.parId(l.id()).orElseThrow().statut());
        assertEquals(1, publies.size());
        assertEquals("u1", publies.get(0).idUsager());
        assertEquals("VELO", publies.get(0).typeVehicule());
    }

    @Test
    void un_usager_avec_une_facture_impayee_ne_peut_pas_louer() {
        ServiceLocation service = service(u -> true);       // Impayes qui dit toujours oui
        assertThrows(FactureImpayee.class,
                () -> service.demarrer(new IdUsager("u3"), new IdVehicule("T118")));
    }

    @Test
    void pas_deux_locations_en_cours_pour_le_meme_usager() {
        ServiceLocation service = service(u -> false);
        service.demarrer(new IdUsager("u1"), new IdVehicule("V428"));
        assertThrows(LocationDejaEnCours.class,
                () -> service.demarrer(new IdUsager("u1"), new IdVehicule("V431")));
    }

    @Test
    void un_vehicule_inconnu_est_refuse() {
        Flotte flotteVide = id -> Optional.empty();
        ServiceLocation service = new ServiceLocation(locations, flotteVide, usagers, u -> false,
                publies::add, horlogeFixe);
        assertThrows(VehiculeInconnu.class,
                () -> service.demarrer(new IdUsager("u1"), new IdVehicule("X999")));
    }

    @Test
    void terminer_une_location_inconnue() {
        assertThrows(LocationInconnue.class,
                () -> service(u -> false).terminer(new IdLocation("X")));
    }
}