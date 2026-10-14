package fr.greenmove.location.domaine;

/** Ce que le domaine de Location a besoin de savoir d'un véhicule : son type et s'il est disponible. Rien d'autre. */
public record Vehicule(IdVehicule id, TypeVehicule type, boolean disponible) {
}
