package fr.greenmove.location.infrastructure.impayes;

import fr.greenmove.facturation.application.ConsulterImpayes;
import fr.greenmove.location.domaine.modele.IdUsager;
import fr.greenmove.location.domaine.ports.Impayes;

public class ImpayesParFacturation implements Impayes {

    private final ConsulterImpayes facturation;

    public ImpayesParFacturation(ConsulterImpayes facturation){
        this.facturation = facturation;
    }

    @Override
    public boolean aUneFactureImpayee(IdUsager usager){
        return facturation.aUneFactureImpayee(usager.valeur());
    }
}
