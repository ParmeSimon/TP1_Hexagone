package fr.greenmove.facturation.domaine.modele;

/** La référence de la transaction renvoyée par le prestataire quand le débit réussit (ex. « SIMU-3F2A9C1B »). */
public record ReferencePaiement(String valeur) {
    public ReferencePaiement {
        if (valeur == null || valeur.isBlank()) {
            throw new IllegalArgumentException("Référence de paiement vide");
        }
    }
}
