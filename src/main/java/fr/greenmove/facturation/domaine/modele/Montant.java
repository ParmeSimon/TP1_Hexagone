package fr.greenmove.facturation.domaine.modele;

/**
 * Value object : une somme d'argent en centimes d'euro.
 *
 * <p>Immuable (un {@code record} : aucune méthode ne le modifie, chaque opération renvoie un nouveau
 * {@code Montant}) et jamais négatif : l'invariant est vérifié à la construction, donc aucun {@code Montant}
 * invalide ne peut exister. Les centimes en {@code long} évitent les erreurs d'arrondi des {@code double}.</p>
 */
public record Montant(long centimes) implements Comparable<Montant> {

    public static final Montant ZERO = new Montant(0);

    public Montant {
        if (centimes < 0) {
            throw new IllegalArgumentException("Un montant ne peut pas être négatif : " + centimes + " centimes");
        }
    }

    public static Montant enCentimes(long centimes) {
        return new Montant(centimes);
    }

    public Montant plus(Montant autre) {
        return new Montant(Math.addExact(centimes, autre.centimes));
    }

    /** Ce montant multiplié par une quantité (ex. le prix d'une minute × le nombre de minutes). */
    public Montant fois(long quantite) {
        if (quantite < 0) {
            throw new IllegalArgumentException("Quantité négative : " + quantite);
        }
        return new Montant(Math.multiplyExact(centimes, quantite));
    }

    /** Ce montant, sans dépasser le plafond. */
    public Montant plafonneA(Montant plafond) {
        return this.compareTo(plafond) > 0 ? plafond : this;
    }

    /**
     * Ce montant diminué d'un pourcentage, arrondi au centime le plus proche (demi-centime vers le haut).
     * Ex. 496 centimes − 20 % = 396,8 → 397 centimes.
     */
    public Montant reduitDe(int pourcentage) {
        if (pourcentage < 0 || pourcentage > 100) {
            throw new IllegalArgumentException("Pourcentage de réduction hors de [0, 100] : " + pourcentage);
        }
        long resteAPayer = Math.multiplyExact(centimes, 100 - pourcentage);
        return new Montant((resteAPayer + 50) / 100);
    }

    public boolean estNul() {
        return centimes == 0;
    }

    @Override
    public int compareTo(Montant autre) {
        return Long.compare(centimes, autre.centimes);
    }

    @Override
    public String toString() {
        return centimes + " centimes";
    }
}
