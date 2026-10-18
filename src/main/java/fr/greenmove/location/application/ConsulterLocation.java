package fr.greenmove.location.application;

import fr.greenmove.location.domaine.modele.IdLocation;
import fr.greenmove.location.domaine.modele.Location;

public interface ConsulterLocation {
    Location consulter(IdLocation id);
}
