package fr.greenmove.facturation.infrastructure.primaire.evenements;

import fr.greenmove.facturation.application.EmettreFacture;
import fr.greenmove.facturation.domaine.modele.IdLocation;
import fr.greenmove.facturation.domaine.modele.IdUsager;
import fr.greenmove.facturation.domaine.modele.TypeVehicule;
import fr.greenmove.location.domaine.evenements.LocationTerminee;
import org.springframework.context.event.EventListener;

/**
 * Adaptateur primaire : il écoute {@link LocationTerminee} sur le bus interne de Spring, la traduit en commande du
 * domaine de Facturation et appelle le port entrant {@link EmettreFacture}.
 *
 * <p>C'est un adaptateur <b>primaire</b> : il appelle un port entrant, il n'en implémente aucun. L'événement arrive
 * en types simples (des chaînes) ; c'est ici que Facturation construit ses propres identifiants, et nulle part
 * ailleurs. Demain, un consommateur Kafka ou un contrôleur HTTP prendrait exactement la même place, et ni
 * {@code ServiceFacturation} ni le domaine ne bougeraient.</p>
 *
 * <p>Un refus de paiement ne remonte pas ici : {@code EmettreFacture} le transforme en facture {@code IMPAYEE}
 * sans exception, donc la fin de la location (côté Location) n'est jamais remise en cause.</p>
 *
 * <p>Seule dépendance vers Location : son événement publié, autorisé par {@code ArchitectureTest}.</p>
 */
public class EcouteurLocationTerminee {

    private final EmettreFacture emettreFacture;

    public EcouteurLocationTerminee(EmettreFacture emettreFacture) {
        this.emettreFacture = emettreFacture;
    }

    @EventListener
    public void surLocationTerminee(LocationTerminee evenement) {
        emettreFacture.emettre(new EmettreFacture.Commande(
                new IdLocation(evenement.idLocation()),
                new IdUsager(evenement.idUsager()),
                TypeVehicule.valueOf(evenement.typeVehicule()),
                evenement.debut(),
                evenement.fin()));
    }
}
