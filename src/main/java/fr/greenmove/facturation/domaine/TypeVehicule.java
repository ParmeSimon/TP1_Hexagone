package fr.greenmove.facturation.domaine;

/**
 * Le type de véhicule, vu par Facturation : la seule chose qu'elle en sait, parce que le prix à la minute en dépend.
 *
 * <p>Volontairement distinct du {@code TypeVehicule} de Location (même noms de constantes, autre type) : Facturation
 * le construit à partir de la chaîne {@code "VELO"} ou {@code "TROTTINETTE"} de l'événement.</p>
 */
public enum TypeVehicule {
    VELO, TROTTINETTE
}
