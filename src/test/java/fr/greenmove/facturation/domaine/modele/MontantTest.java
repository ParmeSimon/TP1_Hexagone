package fr.greenmove.facturation.domaine.modele;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Le value object Montant : jamais négatif, immuable, égal par valeur. Sans Spring. */
class MontantTest {

    @Test
    void un_montant_negatif_n_existe_pas() {
        assertThrows(IllegalArgumentException.class, () -> Montant.enCentimes(-1));
    }

    @Test
    void la_reduction_arrondit_au_centime_le_plus_proche() {
        assertEquals(Montant.enCentimes(397), Montant.enCentimes(496).reduitDe(20)); // 396,8 → 397
        assertEquals(Montant.enCentimes(80), Montant.enCentimes(100).reduitDe(20));
        assertThrows(IllegalArgumentException.class, () -> Montant.enCentimes(100).reduitDe(101));
    }
}
