package fr.greenmove.location.domaine;

/** Identifiant typé d'un véhicule : on ne confond plus un id de véhicule avec un id d'usager (voir ch.2, identifiants typés). */
public record IdVehicule(String valeur) {
    public IdVehicule {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Identifiant de véhicule vide");
        }
    }
}
