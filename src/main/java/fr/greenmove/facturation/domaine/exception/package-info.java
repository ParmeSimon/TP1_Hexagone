/**
 * Les exceptions métier nommées de Facturation : {@code FactureInconnue} (404), {@code FactureDejaPayee} (409),
 * {@code PaiementEncoreRefuse} (502). Un refus de paiement à l'émission n'en fait pas partie : c'est une décision
 * métier, pas une erreur.
 */
package fr.greenmove.facturation.domaine.exception;
