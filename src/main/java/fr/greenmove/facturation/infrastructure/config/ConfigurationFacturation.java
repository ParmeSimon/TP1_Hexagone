package fr.greenmove.facturation.infrastructure.config;

import org.springframework.context.annotation.Configuration;

/**
 * Le câblage de l'hexagone Facturation.
 *
 * <p>À compléter : vos adaptateurs comme beans des ports du domaine ({@code Factures}, {@code Paiement} sur
 * {@code PaiementSimule}, {@code Abonnements} sur {@code UsagersEnMemoire}), puis {@code ServiceFacturation}
 * construit avec ces ports et la grille tarifaire. Déclarez-le sous le type de ses ports entrants : Location a
 * besoin d'un bean {@code ConsulterImpayes}.</p>
 */
@Configuration
public class ConfigurationFacturation {

    // @Bean
    // Paiement paiement(PaiementSimule simulation) { return new PaiementSurSimulation(simulation); }
    //
    // @Bean
    // Abonnements abonnements(UsagersEnMemoire simulation) { return new AbonnementsSurSimulation(simulation); }
    //
    // @Bean
    // Factures factures() { return new FacturesEnMemoire(); }
    //
    // @Bean
    // ServiceFacturation serviceFacturation(Factures factures, Paiement paiement, Abonnements abonnements) {
    //     return new ServiceFacturation(factures, paiement, abonnements, GrilleTarifaire.standard());
    // }
}
