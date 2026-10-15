package fr.greenmove.location.domaine.modele;

public record IdLocation(String valeur) {
    public IdLocation {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Identifiant de véhicule vide");
        }
    }
}
