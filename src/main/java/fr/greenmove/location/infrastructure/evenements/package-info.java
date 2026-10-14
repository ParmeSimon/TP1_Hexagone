/**
 * L'adaptateur de publication des événements de Location.
 *
 * <p>À écrire ici : {@code PublicationSurBusSpring}, qui implémente le port {@code PublicationEvenements} du
 * domaine avec {@code org.springframework.context.ApplicationEventPublisher}. C'est le seul endroit de
 * Location qui sait qu'un bus existe. Demain, le même port sera implémenté par un producteur Kafka, et le
 * cœur ne bougera pas.</p>
 */
package fr.greenmove.location.infrastructure.evenements;
