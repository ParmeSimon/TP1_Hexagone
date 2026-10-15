package fr.greenmove.facturation.infrastructure.persistance;

import fr.greenmove.facturation.domaine.Facture;
import fr.greenmove.facturation.domaine.IdFacture;
import fr.greenmove.facturation.domaine.IdLocation;
import fr.greenmove.facturation.domaine.IdUsager;
import fr.greenmove.facturation.domaine.Montant;
import fr.greenmove.facturation.domaine.ReferencePaiement;
import fr.greenmove.facturation.domaine.StatutFacture;

/**
 * La traduction entre l'agrégat {@link Facture} (types du domaine) et la ligne {@link FactureEntite} (types simples).
 *
 * <p>Dans le sens base → domaine, on passe par {@link Facture#reconstituer} : les invariants de l'agrégat sont
 * revérifiés, une ligne incohérente (PAYEE sans référence) est refusée au lieu de circuler dans le domaine.</p>
 */
final class FactureMapper {

    private FactureMapper() {
    }

    static FactureEntite versEntite(Facture facture) {
        return new FactureEntite(
                facture.id().valeur(),
                facture.idLocation().valeur(),
                facture.idUsager().valeur(),
                facture.montant().centimes(),
                facture.statut().name(),
                facture.referencePaiement().map(ReferencePaiement::valeur).orElse(null));
    }

    static Facture versDomaine(FactureEntite ligne) {
        return Facture.reconstituer(
                new IdFacture(ligne.getId()),
                new IdLocation(ligne.getIdLocation()),
                new IdUsager(ligne.getIdUsager()),
                Montant.enCentimes(ligne.getMontantCentimes()),
                StatutFacture.valueOf(ligne.getStatut()),
                ligne.getReferencePaiement() == null ? null : new ReferencePaiement(ligne.getReferencePaiement()));
    }
}
