package fr.greenmove.facturation.domaine;

/**
 * La location facturée, vue par Facturation : un simple identifiant, jamais l'agrégat Location.
 *
 * <p>C'est le type de Facturation, pas celui de Location : il est construit à partir de la chaîne reçue
 * dans l'événement {@code LocationTerminee}. Les deux hexagones ne partagent aucun value object.</p>
 */
public record IdLocation(String valeur) {
    public IdLocation {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Identifiant de location vide");
        }
    }
}
