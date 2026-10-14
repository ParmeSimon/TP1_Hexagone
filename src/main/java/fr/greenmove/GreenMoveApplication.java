package fr.greenmove;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Le déployable GreenMove du TP1 : un seul processus, deux hexagones (Location et Facturation).
 *
 * <p>C'est le monolithe modulaire du cours : un paquet par hexagone, chacun avec son domaine, son application
 * et son infrastructure. Spring ne touche qu'aux infrastructures. Les deux hexagones se parlent par le bus
 * d'événements interne et par un port, jamais en important le domaine du voisin.</p>
 */
@SpringBootApplication
public class GreenMoveApplication {

    public static void main(String[] args) {
        SpringApplication.run(GreenMoveApplication.class, args);
    }
}
