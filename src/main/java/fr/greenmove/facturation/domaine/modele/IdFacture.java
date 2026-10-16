package fr.greenmove.facturation.domaine.modele;

/** Identifiant typé d'une facture (ex. « F-0001 »). */
public record IdFacture(String valeur) {
    public IdFacture {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Identifiant de facture vide");
        }
    }
}
