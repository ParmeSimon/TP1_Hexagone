package fr.greenmove.facturation.infrastructure.primaire.evenements;

import fr.greenmove.facturation.application.EmettreFacture;
import fr.greenmove.facturation.application.FactureVue;
import fr.greenmove.facturation.domaine.modele.IdLocation;
import fr.greenmove.facturation.domaine.modele.IdUsager;
import fr.greenmove.facturation.domaine.modele.TypeVehicule;
import fr.greenmove.location.domaine.evenements.LocationTerminee;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * L'adaptateur primaire construit à la main, sans Spring ni bus : on lui donne un événement, un double du port
 * entrant {@link EmettreFacture} enregistre la commande reçue.
 */
class EcouteurLocationTermineeTest {

    private static final Instant DEBUT = Instant.parse("2026-10-20T13:40:00Z");
    private static final Instant FIN = Instant.parse("2026-10-20T14:04:00Z");

    private final List<EmettreFacture.Commande> recues = new ArrayList<>();
    private final EmettreFacture emettreFacture = commande -> {
        recues.add(commande);
        return new FactureVue("F-0001", commande.idLocation().valeur(), commande.idUsager().valeur(), 0, "PAYEE", "SIMU-1");
    };

    private final EcouteurLocationTerminee ecouteur = new EcouteurLocationTerminee(emettreFacture);

    @Test
    void traduit_l_evenement_en_commande_avec_les_types_de_facturation() {
        ecouteur.surLocationTerminee(new LocationTerminee("L-0001", "u1", "VELO", DEBUT, FIN));

        assertEquals(1, recues.size());
        EmettreFacture.Commande commande = recues.get(0);
        assertEquals(new IdLocation("L-0001"), commande.idLocation());
        assertEquals(new IdUsager("u1"), commande.idUsager());
        assertEquals(TypeVehicule.VELO, commande.typeVehicule());
        assertEquals(DEBUT, commande.debut());
        assertEquals(FIN, commande.fin());
        assertEquals(24, commande.dureeMinutes());
    }

}
