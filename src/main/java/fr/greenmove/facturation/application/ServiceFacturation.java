package fr.greenmove.facturation.application;

import fr.greenmove.facturation.domaine.Abonnements;
import fr.greenmove.facturation.domaine.Facture;
import fr.greenmove.facturation.domaine.FactureDejaPayee;
import fr.greenmove.facturation.domaine.FactureInconnue;
import fr.greenmove.facturation.domaine.Factures;
import fr.greenmove.facturation.domaine.GrilleTarifaire;
import fr.greenmove.facturation.domaine.IdFacture;
import fr.greenmove.facturation.domaine.IdLocation;
import fr.greenmove.facturation.domaine.IdUsager;
import fr.greenmove.facturation.domaine.Montant;
import fr.greenmove.facturation.domaine.Paiement;
import fr.greenmove.facturation.domaine.PaiementEncoreRefuse;
import fr.greenmove.facturation.domaine.ResultatPaiement;

import java.util.Objects;
import java.util.Optional;

/**
 * Le cas d'usage de Facturation : il implémente les quatre ports entrants.
 *
 * <p>Il orchestre, il ne décide pas : le prix vient de la {@link GrilleTarifaire}, les règles de la facture (statut,
 * référence, régularisation) viennent de l'agrégat {@link Facture}. Il ne reçoit que des <b>ports</b> (des
 * interfaces) par constructeur : aucune classe Spring, aucun adaptateur. On le teste avec des doubles écrits à la
 * main, en quelques millisecondes.</p>
 */
public class ServiceFacturation implements EmettreFacture, ConsulterFacture, RegulariserFacture, ConsulterImpayes {

    private final Factures factures;
    private final Paiement paiement;
    private final Abonnements abonnements;
    private final GrilleTarifaire grille;

    public ServiceFacturation(Factures factures, Paiement paiement, Abonnements abonnements, GrilleTarifaire grille) {
        this.factures = Objects.requireNonNull(factures, "factures");
        this.paiement = Objects.requireNonNull(paiement, "paiement");
        this.abonnements = Objects.requireNonNull(abonnements, "abonnements");
        this.grille = Objects.requireNonNull(grille, "grille");
    }

    // ------------------------------------------------------------------ EmettreFacture

    @Override
    public FactureVue emettre(Commande commande) {
        // Idempotence : le bus peut livrer deux fois le même événement. Si la facture existe, on la renvoie
        // telle quelle, sans recalculer ni redébiter.
        Optional<Facture> dejaEmise = factures.parLocation(commande.idLocation());
        if (dejaEmise.isPresent()) {
            return FactureVue.de(dejaEmise.get());
        }

        boolean abonne = abonnements.estAbonne(commande.idUsager());
        Montant montant = grille.tarifer(commande.typeVehicule(), commande.dureeMinutes(), abonne);
        IdFacture id = factures.prochainIdentifiant();

        // Un refus n'est pas une exception : les deux réponses du prestataire donnent une facture.
        ResultatPaiement resultat = paiement.debiter(commande.idUsager(), montant);
        Facture facture = resultat instanceof ResultatPaiement.Accepte accepte
                ? Facture.payee(id, commande.idLocation(), commande.idUsager(), montant, accepte.reference())
                : Facture.impayee(id, commande.idLocation(), commande.idUsager(), montant);

        factures.enregistrer(facture);
        return FactureVue.de(facture);
    }

    // ------------------------------------------------------------------ ConsulterFacture

    @Override
    public Optional<FactureVue> parId(IdFacture idFacture) {
        return factures.parId(idFacture).map(FactureVue::de);
    }

    @Override
    public Optional<FactureVue> parLocation(IdLocation idLocation) {
        return factures.parLocation(idLocation).map(FactureVue::de);
    }

    // ------------------------------------------------------------------ RegulariserFacture

    @Override
    public FactureVue regulariser(IdFacture idFacture) {
        Facture facture = factures.parId(idFacture).orElseThrow(() -> new FactureInconnue(idFacture));
        if (!facture.estImpayee()) {
            // vérifié AVANT de débiter : on ne fait jamais payer deux fois la même facture
            throw new FactureDejaPayee(idFacture);
        }

        ResultatPaiement resultat = paiement.debiter(facture.idUsager(), facture.montant());
        if (resultat instanceof ResultatPaiement.Refuse refuse) {
            throw new PaiementEncoreRefuse(idFacture, refuse.motif());
        }

        facture.regulariser(((ResultatPaiement.Accepte) resultat).reference());
        factures.enregistrer(facture);
        return FactureVue.de(facture);
    }

    // ------------------------------------------------------------------ ConsulterImpayes (contrat avec Location)

    @Override
    public boolean aUneFactureImpayee(String idUsager) {
        // Location parle en types simples : c'est ici que Facturation construit son propre identifiant.
        return factures.existeUneImpayeePour(new IdUsager(idUsager));
    }
}
