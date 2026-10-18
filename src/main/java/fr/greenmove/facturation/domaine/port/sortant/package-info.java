/**
 * Les <b>ports sortants</b> : ce que le domaine demande au monde extérieur, en vocabulaire métier. Définis ici, par le
 * domaine ; implémentés par les adaptateurs secondaires de {@code infrastructure.secondaire}.
 *
 * <ul>
 *   <li>{@code Factures} : le dépôt des factures ;</li>
 *   <li>{@code Paiement} : débiter un usager chez le prestataire ({@code ResultatPaiement} : accepté ou refusé) ;</li>
 *   <li>{@code Abonnements} : l'usager est-il abonné ?</li>
 * </ul>
 */
package fr.greenmove.facturation.domaine.port.sortant;
