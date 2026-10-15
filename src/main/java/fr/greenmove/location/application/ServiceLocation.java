package fr.greenmove.location.application;

import fr.greenmove.location.domaine.evenements.LocationTerminee;
import fr.greenmove.location.domaine.exceptions.*;
import fr.greenmove.location.domaine.modele.*;
import fr.greenmove.location.domaine.ports.*;

public class ServiceLocation implements DemarrerLocation, ConsulterLocation, TerminerLocation{

    private final Locations locations;
    private final Flotte flotte;
    private final Usagers usagers;
    private final Impayes impayes;
    private final PublicationEvenements publication;
    private final Horloge horloge;

    public ServiceLocation(Locations locations, Flotte flotte, Usagers usagers, Impayes impayes, PublicationEvenements publication, Horloge horloge){
        this.locations = locations;
        this.flotte = flotte;
        this.usagers = usagers;
        this.impayes = impayes;
        this.publication = publication;
        this.horloge = horloge;
    }

    @Override
    public Location demarrer(IdUsager usager, IdVehicule idVehicule){
        Vehicule vehicule = flotte.vehicule(idVehicule).orElseThrow(() -> new VehiculeInconnu(idVehicule));
        if(!usagers.existe(usager)){
            throw new UsagerInconnu(usager);
        }
        if(locations.aUneLocationEnCours(usager)){
            throw new LocationDejaEnCours(usager);
        }
        if(impayes.aUneFactureImpayee(usager)){
            throw new FactureImpayee(usager);
        }
        Location location = Location.demarrer(locations.prochainId(),usager,vehicule,horloge.maintenant());
        locations.sauvegarder(location);
        return location;
    }

    @Override
    public Location terminer(IdLocation id){
        Location location =  consulter(id);
        location.terminer(horloge.maintenant());
        locations.sauvegarder(location);
        publication.publier(new LocationTerminee(
                location.id().valeur(),
                location.usager().valeur(),
                location.type().name(),
                location.debut(),
                location.fin()
        ));
        return location;
    }

    @Override
    public Location consulter(IdLocation id){
        return locations.parId(id).orElseThrow(() -> new LocationInconnue(id));
    }
}
