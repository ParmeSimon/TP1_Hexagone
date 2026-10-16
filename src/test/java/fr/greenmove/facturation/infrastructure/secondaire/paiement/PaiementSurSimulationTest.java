package fr.greenmove.facturation.infrastructure.secondaire.paiement;

import fr.greenmove.exterieur.PaiementSimule;
import fr.greenmove.facturation.domaine.modele.IdUsager;
import fr.greenmove.facturation.domaine.modele.Montant;
import fr.greenmove.facturation.domaine.port.sortant.Paiement;
import fr.greenmove.facturation.domaine.port.sortant.ResultatPaiement;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** L'adaptateur Paiement, construit à la main avec le vrai prestataire simulé, sans Spring. */
class PaiementSurSimulationTest {

    private final Paiement paiement = new PaiementSurSimulation(new PaiementSimule());

    @Test
    void le_refus_du_prestataire_devient_un_resultat_metier_et_non_une_exception() {
        IdUsager u3 = new IdUsager("u3"); // premier prélèvement rejeté par la banque, le second passe

        ResultatPaiement premier = assertDoesNotThrow(() -> paiement.debiter(u3, Montant.enCentimes(80)));
        ResultatPaiement second = paiement.debiter(u3, Montant.enCentimes(80));

        assertInstanceOf(ResultatPaiement.Refuse.class, premier);
        assertTrue(((ResultatPaiement.Refuse) premier).motif().contains("u3"));
        assertTrue(second.estAccepte());
    }

}
