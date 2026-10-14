/**
 * L'adaptateur de l'appel synchrone vers Facturation.
 *
 * <p>À écrire ici : {@code ImpayesParFacturation}, qui implémente le port {@code Impayes} du domaine de Location
 * en appelant le port entrant {@link fr.greenmove.facturation.application.ConsulterImpayes} de Facturation.</p>
 *
 * <p>C'est la seule classe de Location autorisée à importer quelque chose de Facturation, et uniquement son
 * paquet {@code application} (ses ports entrants). Jamais son domaine, jamais son infrastructure. Demain, le
 * même port sera implémenté par un client REST vers le service Facturation.</p>
 */
package fr.greenmove.location.infrastructure.impayes;
