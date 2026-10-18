package fr.greenmove.location.infrastructure.usagers;

import fr.greenmove.exterieur.UsagersEnMemoire;
import fr.greenmove.location.domaine.modele.IdUsager;
import fr.greenmove.location.domaine.ports.Usagers;

public class UsagersSurSimulation implements Usagers {

    private final UsagersEnMemoire simulation;

    public UsagersSurSimulation(UsagersEnMemoire simulation){
        this.simulation = simulation;
    }

    @Override
    public boolean existe(IdUsager id){
        return simulation.parId(id.valeur()).isPresent();
    }
}
