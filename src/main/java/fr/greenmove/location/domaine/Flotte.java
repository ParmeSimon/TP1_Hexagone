package fr.greenmove.location.domaine;

import java.util.Optional;

/**
 * Port sortant : ce que Location demande au service Flotte, en vocabulaire métier et avec les types du domaine.
 *
 * <p>Fourni en exemple, avec son adaptateur {@code FlotteSurSimulation} et son câblage dans
 * {@code ConfigurationLocation} : vos autres ports sortants ({@code Usagers}, {@code Locations}, {@code Horloge},
 * {@code Impayes}, {@code PublicationEvenements}, et côté Facturation {@code Paiement}, {@code Abonnements},
 * {@code Factures}) suivent exactement le même schéma.</p>
 *
 * <p>Le domaine ne sait pas qui répond : une classe en mémoire aujourd'hui, un client REST demain.</p>
 */
public interface Flotte {

    /** Le véhicule, ou vide s'il est inconnu de la flotte. */
    Optional<Vehicule> vehicule(IdVehicule id);
}
