package fr.greenmove.location.domaine.ports;

import fr.greenmove.location.domaine.modele.IdUsager;

public interface Impayes {
    boolean aUneFactureImpayee(IdUsager id);
}
