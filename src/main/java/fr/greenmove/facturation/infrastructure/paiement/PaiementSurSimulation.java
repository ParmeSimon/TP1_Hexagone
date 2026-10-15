package fr.greenmove.facturation.infrastructure.paiement;

import fr.greenmove.exterieur.PaiementSimule;
import fr.greenmove.facturation.domaine.IdUsager;
import fr.greenmove.facturation.domaine.Montant;
import fr.greenmove.facturation.domaine.Paiement;
import fr.greenmove.facturation.domaine.ReferencePaiement;
import fr.greenmove.facturation.domaine.ResultatPaiement;

/**
 * L'adaptateur du port {@link Paiement}, par-dessus le prestataire simulé.
 *
 * <p>Même schéma que {@code FlotteSurSimulation} (implémente le port, reçoit le système extérieur par constructeur,
 * traduit), avec une traduction de plus : le prestataire signale un refus par une <b>exception technique</b>
 * ({@link PaiementSimule.DebitRefuse}). L'adaptateur l'attrape ici et la rend au domaine comme un <b>résultat
 * métier</b> ({@link ResultatPaiement.Refuse}). Aucune exception du prestataire ne traverse cette classe.</p>
 *
 * <p>Quand l'API du prestataire change (une à deux fois par an), c'est cette classe qui change, et elle seule.</p>
 */
public class PaiementSurSimulation implements Paiement {

    private final PaiementSimule prestataire;

    public PaiementSurSimulation(PaiementSimule prestataire) {
        this.prestataire = prestataire;
    }

    @Override
    public ResultatPaiement debiter(IdUsager idUsager, Montant montant) {
        try {
            String reference = prestataire.debiter(idUsager.valeur(), montant.centimes());
            return ResultatPaiement.accepte(new ReferencePaiement(reference));
        } catch (PaiementSimule.DebitRefuse refus) {
            return ResultatPaiement.refuse(refus.getMessage());
        }
    }
}
