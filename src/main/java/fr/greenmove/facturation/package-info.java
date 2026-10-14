/**
 * L'hexagone Facturation : émettre la facture d'une location terminée, la faire payer, savoir qui a une facture
 * impayée, régulariser.
 *
 * <p>Il connaît la grille tarifaire (celle du M01) et les abonnements. Il ne sait pas ce qu'est un véhicule
 * disponible et n'a jamais vu l'agrégat Location : il reçoit des faits (l'événement {@code LocationTerminee}) et
 * en fait une facture. Il répond à Location par son port entrant
 * {@link fr.greenmove.facturation.application.ConsulterImpayes}.</p>
 */
package fr.greenmove.facturation;
