package fr.greenmove.location.infrastructure.config;

import fr.greenmove.exterieur.FlotteEnMemoire;
import fr.greenmove.exterieur.UsagersEnMemoire;
import fr.greenmove.facturation.application.ConsulterImpayes;
import fr.greenmove.location.application.ServiceLocation;
import fr.greenmove.location.domaine.ports.*;
import fr.greenmove.location.infrastructure.evenements.PublicationSurBusSpring;
import fr.greenmove.location.infrastructure.flotte.FlotteSurSimulation;
import fr.greenmove.location.infrastructure.horloge.HorlogeSysteme;
import fr.greenmove.location.infrastructure.impayes.ImpayesParFacturation;
import fr.greenmove.location.infrastructure.persistance.LocationJpaRepository;
import fr.greenmove.location.infrastructure.persistance.LocationsJpa;
import fr.greenmove.location.infrastructure.usagers.UsagersSurSimulation;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Le câblage de l'hexagone Location : la seule classe qui sait quelle implémentation se cache derrière chaque port.
 *
 * <p>Le principe, montré avec le port {@link Flotte} : chaque méthode {@code @Bean} construit un adaptateur et le
 * déclare <b>sous le type de son port</b>. Spring fournit en paramètre les beans dont la méthode a besoin (ici
 * {@code FlotteEnMemoire}, déclaré dans {@code ConfigurationExterieur}). Le cas d'usage reçoit ensuite les ports
 * par constructeur, sans savoir qui les implémente. Ni le domaine ni l'application ne portent d'annotation.</p>
 *
 * <p>À compléter : les autres ports sur le même modèle, puis {@code ServiceLocation}.</p>
 */
@Configuration
public class ConfigurationLocation {

    /** L'exemple fourni : le port Flotte est servi par l'adaptateur FlotteSurSimulation. */
    @Bean
    Flotte flotte(FlotteEnMemoire simulation) {
        return new FlotteSurSimulation(simulation);
    }

     @Bean
     Usagers usagers(UsagersEnMemoire simulation) { return new UsagersSurSimulation(simulation); }

     @Bean
     Horloge horloge() { return new HorlogeSysteme(); }

     @Bean
     Impayes impayes(ConsulterImpayes consulterImpayes) { return new ImpayesParFacturation(consulterImpayes); }

     @Bean
     PublicationEvenements publication(ApplicationEventPublisher bus) { return new PublicationSurBusSpring(bus); }

     @Bean
     Locations locations(LocationJpaRepository jpa) { return new LocationsJpa(jpa); }

     @Bean
     ServiceLocation serviceLocation(Locations locations, Flotte flotte, Usagers usagers, Impayes impayes,
                                     PublicationEvenements publication, Horloge horloge) {
         return new ServiceLocation(locations, flotte, usagers, impayes, publication, horloge);
     }
}
