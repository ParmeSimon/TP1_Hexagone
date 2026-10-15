package fr.greenmove.location.domaine.ports;

import java.time.Instant;

public interface Horloge {
    Instant maintenant();
}
