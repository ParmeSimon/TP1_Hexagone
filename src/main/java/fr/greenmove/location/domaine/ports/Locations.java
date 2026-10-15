package fr.greenmove.location.domaine.ports;

import fr.greenmove.location.domaine.modele.IdLocation;
import fr.greenmove.location.domaine.modele.IdUsager;
import fr.greenmove.location.domaine.modele.Location;

import java.util.Optional;

public interface Locations {
    IdLocation prochainId();
    void sauvegarder(Location location);
    Optional<Location> parId(IdLocation id);
    boolean aUneLocationEnCours(IdUsager usager);
}
