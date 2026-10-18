package fr.greenmove.location.infrastructure.evenements;

import fr.greenmove.location.domaine.evenements.LocationTerminee;
import fr.greenmove.location.domaine.ports.PublicationEvenements;
import org.springframework.context.ApplicationEventPublisher;

public class PublicationSurBusSpring implements PublicationEvenements {

    private final ApplicationEventPublisher bus;

    public PublicationSurBusSpring(ApplicationEventPublisher bus){
        this.bus = bus;
    }

    @Override
    public void publier(LocationTerminee evenement){
        bus.publishEvent(evenement);
    }
}
