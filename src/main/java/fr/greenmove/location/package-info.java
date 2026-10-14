/**
 * L'hexagone Location : démarrer, terminer et consulter une location.
 *
 * <p>Il connaît la flotte (un véhicule existe-t-il, est-il disponible ?) et les usagers (existent-ils ?). Il ne
 * connaît aucun tarif et ne parle jamais au prestataire de paiement : quand une location se termine, il publie
 * l'événement {@link fr.greenmove.location.domaine.evenements.LocationTerminee} et c'est tout. Avant de démarrer
 * une location, il demande à son port {@code Impayes} si l'usager a une facture impayée.</p>
 */
package fr.greenmove.location;
