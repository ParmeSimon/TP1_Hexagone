package fr.greenmove.facturation.application;

/**
 * Port entrant de Facturation appelé par Location avant de démarrer une location : c'est le second
 * <b>contrat</b> entre les deux hexagones.
 *
 * <p>Il parle en types simples, comme l'événement {@code LocationTerminee} : l'hexagone qui l'appelle n'importe
 * aucun value object de Facturation. Votre cas d'usage ({@code ServiceFacturation}) l'implémente avec les autres
 * ports entrants. Côté Location, l'adaptateur {@code ImpayesParFacturation} l'appelle derrière le port
 * {@code Impayes}.</p>
 */
public interface ConsulterImpayes {

    /** Vrai si l'usager a au moins une facture en statut {@code IMPAYEE}. */
    boolean aUneFactureImpayee(String idUsager);
}
