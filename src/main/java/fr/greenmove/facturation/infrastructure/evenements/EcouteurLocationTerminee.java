package fr.greenmove.facturation.infrastructure.evenements;

import fr.greenmove.facturation.application.EmettreFacture;
import fr.greenmove.facturation.domaine.IdLocation;
import fr.greenmove.facturation.domaine.IdUsager;
import fr.greenmove.facturation.domaine.TypeVehicule;
import fr.greenmove.location.domaine.evenements.LocationTerminee;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Adaptateur primaire de Facturation : il écoute {@link LocationTerminee} sur le bus interne de Spring, traduit
 * l'événement (des types simples) en {@link EmettreFacture.Commande} (les types de Facturation) et appelle le port
 * entrant {@link EmettreFacture}.
 *
 * <p>C'est la seule classe de Facturation qui importe quelque chose de Location, et uniquement son événement publié.
 * Location ne sait pas qu'elle existe. Demain, un consommateur Kafka ou un endpoint HTTP fera la même chose au même
 * endroit, sans toucher au cœur.</p>
 */
@Component
public class EcouteurLocationTerminee {

    private final EmettreFacture emettreFacture;

    public EcouteurLocationTerminee(EmettreFacture emettreFacture) {
        this.emettreFacture = emettreFacture;
    }

    @EventListener
    public void quandUneLocationEstTerminee(LocationTerminee evenement) {
        emettreFacture.emettre(new EmettreFacture.Commande(
                new IdLocation(evenement.idLocation()),
                new IdUsager(evenement.idUsager()),
                TypeVehicule.valueOf(evenement.typeVehicule()),
                evenement.debut(),
                evenement.fin()));
    }
}
