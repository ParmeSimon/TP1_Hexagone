/**
 * L'adaptateur primaire qui écoute le bus : {@code EcouteurLocationTerminee}.
 *
 * <p>Une méthode annotée {@code @EventListener} reçoit
 * {@link fr.greenmove.location.domaine.evenements.LocationTerminee}, la traduit en commande du domaine de
 * Facturation (ses propres types) et appelle le port entrant {@code EmettreFacture}. C'est un adaptateur
 * primaire : il appelle un port entrant, il n'en implémente aucun. Demain, un consommateur Kafka fera la même
 * chose au même endroit.</p>
 *
 * <p>C'est la seule classe de Facturation autorisée à importer quelque chose de Location, et uniquement ses
 * événements publiés et ses ports entrants. Jamais son domaine, jamais son infrastructure.</p>
 */
package fr.greenmove.facturation.infrastructure.evenements;
