package fr.greenmove.location.infrastructure.flotte;

import fr.greenmove.exterieur.FlotteEnMemoire;
import fr.greenmove.location.domaine.Flotte;
import fr.greenmove.location.domaine.IdVehicule;
import fr.greenmove.location.domaine.TypeVehicule;
import fr.greenmove.location.domaine.Vehicule;

import java.util.Optional;

/**
 * L'adaptateur du port {@link Flotte}, par-dessus la simulation du service Flotte. <b>Fourni en exemple.</b>
 *
 * <p>Trois choses à retenir, valables pour tous vos adaptateurs secondaires :</p>
 * <ol>
 *   <li>Il <b>implémente le port</b> du domaine. C'est l'adaptateur qui dépend du port, jamais l'inverse.</li>
 *   <li>Il <b>reçoit le système extérieur par constructeur</b> : pas de {@code new FlotteEnMemoire()} ici, c'est le
 *       câblage ({@code ConfigurationLocation}) qui décide quelle instance lui donner.</li>
 *   <li>Il <b>traduit</b> : {@code EntreeFlotte} (des chaînes et un booléen) devient {@code Vehicule} (des types du
 *       domaine). Rien du monde extérieur ne traverse cette classe.</li>
 * </ol>
 *
 * <p>Aucune annotation Spring : l'adaptateur est une classe Java ordinaire, testable sans contexte
 * (voir {@code FlotteSurSimulationTest}). Le jour où Flotte devient un vrai service, on écrit
 * {@code FlotteParRest implements Flotte} et on change une ligne dans la configuration.</p>
 */
public class FlotteSurSimulation implements Flotte {

    private final FlotteEnMemoire simulation;

    public FlotteSurSimulation(FlotteEnMemoire simulation) {
        this.simulation = simulation;
    }

    @Override
    public Optional<Vehicule> vehicule(IdVehicule id) {
        return simulation.parId(id.valeur())
                .map(entree -> new Vehicule(id, TypeVehicule.valueOf(entree.type()), entree.disponible()));
    }
}
