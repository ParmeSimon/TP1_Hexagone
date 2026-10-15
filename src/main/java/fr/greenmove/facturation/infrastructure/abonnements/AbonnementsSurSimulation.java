package fr.greenmove.facturation.infrastructure.abonnements;

import fr.greenmove.exterieur.UsagersEnMemoire;
import fr.greenmove.facturation.domaine.Abonnements;
import fr.greenmove.facturation.domaine.IdUsager;

/**
 * L'adaptateur du port {@link Abonnements}, par-dessus la simulation du service Compte usager.
 *
 * <p>Location lit la même simulation avec son propre port ({@code Usagers}) et son propre adaptateur : chacun pose
 * sa question, aucun ne partage le port de l'autre. Ici, on ne retient de l'usager qu'une chose : est-il abonné ?
 * Un usager inconnu est traité comme non abonné (il paie plein tarif).</p>
 */
public class AbonnementsSurSimulation implements Abonnements {

    private final UsagersEnMemoire simulation;

    public AbonnementsSurSimulation(UsagersEnMemoire simulation) {
        this.simulation = simulation;
    }

    @Override
    public boolean estAbonne(IdUsager idUsager) {
        return simulation.parId(idUsager.valeur())
                .map(UsagersEnMemoire.EntreeUsager::abonne)
                .orElse(false);
    }
}
