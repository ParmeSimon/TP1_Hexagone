/**
 * L'adaptateur de persistance de Location : JPA sur H2, table {@code location}.
 *
 * <p>À écrire ici : {@code LocationEntity} (l'entité JPA, package-private, constructeur vide, champs),
 * {@code LocationJpaRepository} (Spring Data), {@code LocationsJpa} (l'adaptateur qui implémente le port
 * {@code Locations} du domaine) et le mapper entre l'entité et {@code Location}.</p>
 *
 * <p>L'entité n'est pas l'objet du domaine. Et elle n'a aucune relation JPA vers la table des factures :
 * chaque hexagone a ses tables.</p>
 */
package fr.greenmove.location.infrastructure.persistance;
