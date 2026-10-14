/**
 * Le domaine de Location : le centre de l'hexagone.
 *
 * <p>À écrire ici : {@code Location} (l'agrégat et ses règles : démarrer, terminer, pas deux fois, fin après
 * début, durée en minutes entières), {@code TypeVehicule}, {@code Statut}, les identifiants typés
 * ({@code IdLocation}, {@code IdUsager}, {@code IdVehicule}), les exceptions métier nommées, et les
 * <b>ports sortants</b> que le domaine définit : {@code Locations} (le dépôt), {@code Usagers}, {@code Horloge},
 * {@code Impayes}, {@code PublicationEvenements}. Le port {@code Flotte} est <b>fourni en exemple</b> avec {@code Vehicule},
 * {@code IdVehicule} et {@code TypeVehicule} : faites les autres sur le même modèle.</p>
 *
 * <p>Pas de tarif ici : le montant d'une location est l'affaire de Facturation.</p>
 *
 * <p>Interdit ici : tout import de {@code org.springframework}, {@code jakarta.persistence}, {@code com.fasterxml},
 * de {@code fr.greenmove.exterieur}, de {@code fr.greenmove.facturation}, et tout appel à {@code Instant.now()}.
 * Le test {@code ArchitectureTest} le vérifie.</p>
 */
package fr.greenmove.location.domaine;
