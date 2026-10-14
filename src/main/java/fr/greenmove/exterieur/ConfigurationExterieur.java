package fr.greenmove.exterieur;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Les trois simulations, déclarées une fois pour tout le déployable. Vos adaptateurs les reçoivent par injection. */
@Configuration
public class ConfigurationExterieur {

    @Bean
    FlotteEnMemoire flotteEnMemoire() {
        return new FlotteEnMemoire();
    }

    @Bean
    UsagersEnMemoire usagersEnMemoire() {
        return new UsagersEnMemoire();
    }

    @Bean
    PaiementSimule paiementSimule() {
        return new PaiementSimule();
    }
}
