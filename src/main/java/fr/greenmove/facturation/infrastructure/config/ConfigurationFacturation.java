package fr.greenmove.facturation.infrastructure.config;

import fr.greenmove.exterieur.PaiementSimule;
import fr.greenmove.exterieur.UsagersEnMemoire;
import fr.greenmove.facturation.application.ServiceFacturation;
import fr.greenmove.facturation.domaine.Abonnements;
import fr.greenmove.facturation.domaine.Factures;
import fr.greenmove.facturation.domaine.GrilleTarifaire;
import fr.greenmove.facturation.domaine.Paiement;
import fr.greenmove.facturation.infrastructure.abonnements.AbonnementsSurSimulation;
import fr.greenmove.facturation.infrastructure.paiement.PaiementSurSimulation;
import fr.greenmove.facturation.infrastructure.persistance.FactureJpaRepository;
import fr.greenmove.facturation.infrastructure.persistance.FacturesJpa;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Le câblage de l'hexagone Facturation : la seule classe qui sait quelle implémentation se cache derrière chaque port.
 *
 * <p>Même principe que {@code ConfigurationLocation} : chaque adaptateur est déclaré <b>sous le type de son port</b>,
 * puis le cas d'usage reçoit les ports (les interfaces), jamais les adaptateurs. Changer de prestataire de paiement,
 * passer le dépôt en mémoire ou changer de grille tarifaire = changer une ligne ici, rien dans le domaine.</p>
 */
@Configuration
public class ConfigurationFacturation {

    @Bean
    Paiement paiement(PaiementSimule simulation) {
        return new PaiementSurSimulation(simulation);
    }

    @Bean
    Abonnements abonnements(UsagersEnMemoire simulation) {
        return new AbonnementsSurSimulation(simulation);
    }

    @Bean
    Factures factures(FactureJpaRepository jpa) {
        return new FacturesJpa(jpa);
    }

    /** La grille en vigueur. La nouvelle grille du marketing se branche ici. */
    @Bean
    GrilleTarifaire grilleTarifaire() {
        return GrilleTarifaire.standard();
    }

    /**
     * Le cas d'usage. Son type ({@code ServiceFacturation}) implémente les quatre ports entrants : Spring le fournit
     * donc à qui demande {@code EmettreFacture}, {@code ConsulterFacture}, {@code RegulariserFacture} (le contrôleur,
     * l'écouteur) ou {@code ConsulterImpayes} (l'adaptateur {@code Impayes} de Location).
     */
    @Bean
    ServiceFacturation serviceFacturation(Factures factures, Paiement paiement, Abonnements abonnements,
                                          GrilleTarifaire grilleTarifaire) {
        return new ServiceFacturation(factures, paiement, abonnements, grilleTarifaire);
    }
}
