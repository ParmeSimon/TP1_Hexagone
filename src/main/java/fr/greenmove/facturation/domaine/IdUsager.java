package fr.greenmove.facturation.domaine;

/** L'usager facturé, vu par Facturation. Type propre à Facturation : il ne réutilise pas celui de Location. */
public record IdUsager(String valeur) {
    public IdUsager {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Identifiant d'usager vide");
        }
    }
}
