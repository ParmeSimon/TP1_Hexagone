package fr.greenmove.location.infrastructure.rest;

import fr.greenmove.location.application.ConsulterLocation;
import fr.greenmove.location.application.DemarrerLocation;
import fr.greenmove.location.application.TerminerLocation;
import fr.greenmove.location.domaine.modele.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;

@RestController
@RequestMapping("/locations")
public class LocationController {

    private final DemarrerLocation demarrerLocation;
    private final TerminerLocation terminerLocation;
    private final ConsulterLocation consulterLocation;

    public LocationController(DemarrerLocation demarrerLocation,
                              TerminerLocation terminerLocation,
                              ConsulterLocation consulterLocation) {
        this.demarrerLocation = demarrerLocation;
        this.terminerLocation = terminerLocation;
        this.consulterLocation = consulterLocation;
    }

    // ---------- Les formats JSON (annexe A) ----------
    public record DemandeDemarrage(String usager, String vehicule) { }

    public record LocationDemarreeDto(String id, String usager, String vehicule, String type,
                                      Instant debut, String statut) { }

    public record LocationFinieDto(String id, Instant fin, Long dureeMinutes, String statut) { }

    public record LocationDto(String id, String usager, String vehicule, String type,
                              Instant debut, Instant fin, Long dureeMinutes, String statut) { }

    // ---------- POST /locations → 201 ----------
    @PostMapping
    public ResponseEntity<LocationDemarreeDto> demarrer(@RequestBody DemandeDemarrage demande) {
        Location l = demarrerLocation.demarrer(
                new IdUsager(demande.usager()),
                new IdVehicule(demande.vehicule()));

        return ResponseEntity.status(HttpStatus.CREATED).body(new LocationDemarreeDto(
                l.id().valeur(), l.usager().valeur(), l.vehicule().valeur(),
                l.type().name(), l.debut(), l.statut().name()));
    }

    // ---------- POST /locations/{id}/fin → 200 ----------
    @PostMapping("/{id}/fin")
    public LocationFinieDto terminer(@PathVariable("id") String id) {
        Location l = terminerLocation.terminer(new IdLocation(id));
        return new LocationFinieDto(l.id().valeur(), l.fin(), l.dureeMinutes(), l.statut().name());
    }

    // ---------- GET /locations/{id} → 200 ----------
    @GetMapping("/{id}")
    public LocationDto consulter(@PathVariable("id") String id) {
        Location l = consulterLocation.consulter(new IdLocation(id));
        return new LocationDto(l.id().valeur(), l.usager().valeur(), l.vehicule().valeur(),
                l.type().name(), l.debut(), l.fin(), l.dureeMinutes(), l.statut().name());
    }
}