/**
 * Le domaine de Facturation : le centre de l'hexagone.
 *
 * <p>À écrire ici : {@code Facture} (l'agrégat : une facture par location, statuts {@code PAYEE} /
 * {@code IMPAYEE}, référence de paiement, régularisation), {@code Montant} (value object en centimes, jamais
 * négatif), {@code GrilleTarifaire} (le calcul du M01, séparé de Facture et remplaçable), les identifiants typés
 * ({@code IdFacture}, et les siens pour la location et l'usager : Facturation ne réutilise pas ceux de Location),
 * les exceptions métier, et les <b>ports sortants</b> : {@code Factures} (le dépôt), {@code Paiement},
 * {@code Abonnements}.</p>
 *
 * <p>Interdit ici : tout import de {@code org.springframework}, {@code jakarta.persistence}, {@code com.fasterxml},
 * de {@code fr.greenmove.exterieur}, de {@code fr.greenmove.location}, et tout appel à {@code Instant.now()}.</p>
 */
package fr.greenmove.facturation.domaine;
