package fr.greenmove.facturation.domaine.tarif;

import fr.greenmove.facturation.domaine.modele.Montant;
import fr.greenmove.facturation.domaine.modele.TypeVehicule;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/**
 * La grille du M01 : prise en charge + prix à la minute selon le véhicule, plafonné, puis réduction abonné
 * appliquée <b>après</b> le plafond.
 *
 * <p>Les valeurs sont des paramètres du constructeur : une nouvelle grille avec la même formule n'est qu'une autre
 * instance. {@link #standard()} donne les valeurs du M01 : 1,00 € de prise en charge, 0,15 €/min en vélo,
 * 0,22 €/min en trottinette, plafond de 15,00 €, réduction abonné de 20 %.</p>
 *
 * <p>Hypothèse : le plafond s'applique à la location (les locations de plus d'une journée ne sont pas traitées
 * à part).</p>
 */
public final class GrilleTarifaireM01 implements GrilleTarifaire {

    private final Montant priseEnCharge;
    private final Map<TypeVehicule, Montant> prixParMinute;
    private final Montant plafond;
    private final int reductionAbonnePourcent;

    public GrilleTarifaireM01(Montant priseEnCharge, Montant prixMinuteVelo, Montant prixMinuteTrottinette,
                              Montant plafond, int reductionAbonnePourcent) {
        this.priseEnCharge = Objects.requireNonNull(priseEnCharge, "priseEnCharge");
        this.plafond = Objects.requireNonNull(plafond, "plafond");
        if (reductionAbonnePourcent < 0 || reductionAbonnePourcent > 100) {
            throw new IllegalArgumentException("Réduction abonné hors de [0, 100] : " + reductionAbonnePourcent);
        }
        this.reductionAbonnePourcent = reductionAbonnePourcent;
        this.prixParMinute = new EnumMap<>(TypeVehicule.class);
        this.prixParMinute.put(TypeVehicule.VELO, Objects.requireNonNull(prixMinuteVelo, "prixMinuteVelo"));
        this.prixParMinute.put(TypeVehicule.TROTTINETTE,
                Objects.requireNonNull(prixMinuteTrottinette, "prixMinuteTrottinette"));
    }

    /** Les valeurs du M01. */
    public static GrilleTarifaireM01 standard() {
        return new GrilleTarifaireM01(
                Montant.enCentimes(100),   // prise en charge
                Montant.enCentimes(15),    // vélo, par minute
                Montant.enCentimes(22),    // trottinette, par minute
                Montant.enCentimes(1500),  // plafond
                20);                       // réduction abonné, en %
    }

    @Override
    public Montant tarifer(TypeVehicule type, long dureeMinutes, boolean abonne) {
        Objects.requireNonNull(type, "type");
        if (dureeMinutes < 0) {
            throw new IllegalArgumentException("Durée négative : " + dureeMinutes + " minutes");
        }
        Montant brut = priseEnCharge.plus(prixParMinute.get(type).fois(dureeMinutes));
        Montant plafonne = brut.plafonneA(plafond);
        return abonne ? plafonne.reduitDe(reductionAbonnePourcent) : plafonne;
    }
}
