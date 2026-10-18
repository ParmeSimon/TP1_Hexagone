package fr.greenmove.location.domaine.modele;

public record IdUsager(String valeur) {
    public IdUsager {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Identifiant d'usager vide");
        }
    }
}
