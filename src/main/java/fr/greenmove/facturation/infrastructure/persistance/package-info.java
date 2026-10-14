/**
 * L'adaptateur de persistance de Facturation : le dépôt des factures, qui implémente le port {@code Factures}.
 *
 * <p>En mémoire (une {@code Map}) ou en JPA sur sa propre table {@code facture}, à votre choix. Dans les deux
 * cas : une facture connaît sa location par un identifiant, jamais par une relation JPA vers la table
 * {@code location}. Chaque hexagone a ses tables.</p>
 */
package fr.greenmove.facturation.infrastructure.persistance;
