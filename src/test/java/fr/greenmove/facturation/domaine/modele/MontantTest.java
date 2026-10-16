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
    void zero_est_un_montant_valide() {
        assertTrue(Montant.enCentimes(0).estNul());
        assertEquals(Montant.ZERO, Montant.enCentimes(0));
    }

    @Test
    void deux_montants_de_meme_valeur_sont_egaux() {
        assertEquals(Montant.enCentimes(460), Montant.enCentimes(460));
    }

    @Test
    void les_operations_renvoient_un_nouveau_montant_sans_modifier_l_original() {
        Montant cent = Montant.enCentimes(100);

        Montant somme = cent.plus(Montant.enCentimes(360));

        assertEquals(Montant.enCentimes(460), somme);
        assertEquals(Montant.enCentimes(100), cent);
    }

    @Test
    void multiplier_par_une_quantite() {
        assertEquals(Montant.enCentimes(360), Montant.enCentimes(15).fois(24));
        assertThrows(IllegalArgumentException.class, () -> Montant.enCentimes(15).fois(-1));
    }

    @Test
    void plafonner() {
        Montant plafond = Montant.enCentimes(1500);
        assertEquals(plafond, Montant.enCentimes(4600).plafonneA(plafond));
        assertEquals(Montant.enCentimes(460), Montant.enCentimes(460).plafonneA(plafond));
    }

    @Test
    void la_reduction_arrondit_au_centime_le_plus_proche() {
        assertEquals(Montant.enCentimes(397), Montant.enCentimes(496).reduitDe(20)); // 396,8 → 397
        assertEquals(Montant.enCentimes(80), Montant.enCentimes(100).reduitDe(20));
        assertThrows(IllegalArgumentException.class, () -> Montant.enCentimes(100).reduitDe(101));
    }
}
