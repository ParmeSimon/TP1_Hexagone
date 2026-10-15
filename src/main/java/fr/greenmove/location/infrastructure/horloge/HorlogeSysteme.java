package fr.greenmove.location.infrastructure.horloge;

import fr.greenmove.location.domaine.ports.Horloge;

import java.time.Instant;

public class HorlogeSysteme implements Horloge {

    @Override
    public Instant maintenant(){
        return Instant.now();
    }
}
