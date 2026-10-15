package fr.greenmove.facturation.domaine;

import org.junit.jupiter.api.Test;

import static fr.greenmove.facturation.domaine.TypeVehicule.TROTTINETTE;
import static fr.greenmove.facturation.domaine.TypeVehicule.VELO;
import static org.junit.jupiter.api.Assertions.*;

/** Les cas chiffrés de la grille du M01 (énoncé et README). Sans Spring. */
class GrilleTarifaireTest {

    private static final boolean ABONNE = true;
    private static final boolean NON_ABONNE = false;

    private final GrilleTarifaire grille = GrilleTarifaire.standard();

    @Test
    void velo_non_abonne_24_minutes() {
        // 100 + 24 × 15 = 460
        assertEquals(Montant.enCentimes(460), grille.tarifer(VELO, 24, NON_ABONNE));
    }

    @Test
    void trottinette_abonne_18_minutes() {
        // 100 + 18 × 22 = 496, puis − 20 % = 396,8 → 397
        assertEquals(Montant.enCentimes(397), grille.tarifer(TROTTINETTE, 18, ABONNE));
    }

    @Test
    void plafond_5_heures_de_velo() {
        // 100 + 300 × 15 = 4 600, plafonné à 1 500
        assertEquals(Montant.enCentimes(1500), grille.tarifer(VELO, 300, NON_ABONNE));
    }

    @Test
    void la_reduction_abonne_s_applique_apres_le_plafond() {
        // 1 500 − 20 % = 1 200 (et non 4 600 − 20 % = 3 680, plafonné à 1 500)
        assertEquals(Montant.enCentimes(1200), grille.tarifer(VELO, 300, ABONNE));
    }

    @Test
    void zero_minute_coute_la_prise_en_charge() {
        assertEquals(Montant.enCentimes(100), grille.tarifer(VELO, 0, NON_ABONNE));
        assertEquals(Montant.enCentimes(80), grille.tarifer(TROTTINETTE, 0, ABONNE));
    }

    @Test
    void une_duree_negative_est_refusee() {
        assertThrows(IllegalArgumentException.class, () -> grille.tarifer(VELO, -1, NON_ABONNE));
    }

    @Test
    void la_grille_est_remplacable() {
        // une nouvelle grille marketing : mêmes règles, autres valeurs, rien à changer ailleurs
        GrilleTarifaire promo = new GrilleTarifaireM01(
                Montant.ZERO, Montant.enCentimes(10), Montant.enCentimes(20), Montant.enCentimes(1000), 50);
        assertEquals(Montant.enCentimes(240), promo.tarifer(VELO, 24, NON_ABONNE));

        // ou une tout autre formule, même en lambda
        GrilleTarifaire forfait = (type, minutes, abonne) -> Montant.enCentimes(300);
        assertEquals(Montant.enCentimes(300), forfait.tarifer(TROTTINETTE, 42, ABONNE));
    }
}
