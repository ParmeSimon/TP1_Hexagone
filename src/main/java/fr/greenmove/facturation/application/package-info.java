/**
 * L'application de Facturation : les cas d'usage.
 *
 * <p>À écrire ici : les <b>ports entrants</b> {@code EmettreFacture} (à partir des faits d'une location
 * terminée : montant selon la grille et l'abonnement, débit chez le prestataire, facture {@code PAYEE} ou
 * {@code IMPAYEE}), {@code ConsulterFacture}, {@code RegulariserFacture} (nouvelle tentative de débit d'une
 * facture impayée), et {@link ConsulterImpayes}, déjà fourni parce que Location l'appelle. Puis le ou les cas
 * d'usage qui les implémentent, par exemple {@code ServiceFacturation}.</p>
 *
 * <p>Émettre deux fois la facture de la même location doit renvoyer la même facture (idempotence) : le bus
 * peut livrer un événement deux fois.</p>
 */
package fr.greenmove.facturation.application;
