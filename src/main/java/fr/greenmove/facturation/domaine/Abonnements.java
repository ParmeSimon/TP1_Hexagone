package fr.greenmove.facturation.domaine;

/**
 * Port sortant : savoir si un usager est abonné, pour lui appliquer la réduction de la grille.
 *
 * <p>C'est la question de Facturation au service Compte usager. Location pose une autre question au même service
 * (« l'usager existe-t-il ? ») avec son propre port {@code Usagers} : chacun son port, aucun partage. L'adaptateur
 * (par-dessus {@code UsagersEnMemoire}) répond {@code false} pour un usager inconnu.</p>
 */
public interface Abonnements {

    /** Vrai si l'usager a un abonnement en cours ; faux s'il n'en a pas ou s'il est inconnu. */
    boolean estAbonne(IdUsager idUsager);
}
