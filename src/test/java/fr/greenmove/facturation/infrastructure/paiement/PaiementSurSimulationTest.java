package fr.greenmove.facturation.infrastructure.paiement;

import fr.greenmove.exterieur.PaiementSimule;
import fr.greenmove.facturation.domaine.IdUsager;
import fr.greenmove.facturation.domaine.Montant;
import fr.greenmove.facturation.domaine.Paiement;
import fr.greenmove.facturation.domaine.ResultatPaiement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** L'adaptateur Paiement, construit à la main avec le vrai prestataire simulé, sans Spring. */
class PaiementSurSimulationTest {

    private final Paiement paiement = new PaiementSurSimulation(new PaiementSimule());

    @Test
    void un_debit_accepte_donne_la_reference_du_prestataire() {
        ResultatPaiement resultat = paiement.debiter(new IdUsager("u1"), Montant.enCentimes(100));

        assertTrue(resultat.estAccepte());
        assertTrue(((ResultatPaiement.Accepte) resultat).reference().valeur().startsWith("SIMU-"));
    }

    @Test
    void le_refus_du_prestataire_devient_un_resultat_metier_et_non_une_exception() {
        IdUsager u3 = new IdUsager("u3"); // premier prélèvement rejeté par la banque, le second passe

        ResultatPaiement premier = assertDoesNotThrow(() -> paiement.debiter(u3, Montant.enCentimes(80)));
        ResultatPaiement second = paiement.debiter(u3, Montant.enCentimes(80));

        assertInstanceOf(ResultatPaiement.Refuse.class, premier);
        assertTrue(((ResultatPaiement.Refuse) premier).motif().contains("u3"));
        assertTrue(second.estAccepte());
    }

    @Test
    void un_montant_nul_refuse_par_le_prestataire_est_aussi_un_refus() {
        assertFalse(paiement.debiter(new IdUsager("u1"), Montant.ZERO).estAccepte());
    }
}
