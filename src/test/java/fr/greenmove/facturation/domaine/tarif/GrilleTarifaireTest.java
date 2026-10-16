package fr.greenmove.facturation.domaine.tarif;

import fr.greenmove.facturation.domaine.modele.Montant;
import org.junit.jupiter.api.Test;

import static fr.greenmove.facturation.domaine.modele.TypeVehicule.TROTTINETTE;
import static fr.greenmove.facturation.domaine.modele.TypeVehicule.VELO;
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

}
