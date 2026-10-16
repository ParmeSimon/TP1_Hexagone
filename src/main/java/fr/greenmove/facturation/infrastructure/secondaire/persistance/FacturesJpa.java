package fr.greenmove.facturation.infrastructure.secondaire.persistance;

import fr.greenmove.facturation.domaine.modele.Facture;
import fr.greenmove.facturation.domaine.port.sortant.Factures;
import fr.greenmove.facturation.domaine.modele.IdFacture;
import fr.greenmove.facturation.domaine.modele.IdLocation;
import fr.greenmove.facturation.domaine.modele.IdUsager;
import fr.greenmove.facturation.domaine.modele.StatutFacture;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

/**
 * L'adaptateur du port {@link Factures}, en JPA sur la table {@code facture} (H2 en mémoire dans ce TP).
 *
 * <p>Il implémente le port du domaine, reçoit le repository Spring Data par constructeur et traduit dans les deux sens
 * avec {@link FactureMapper}. Comme les autres adaptateurs, il ne porte aucune annotation Spring : c'est
 * {@code ConfigurationFacturation} qui le déclare comme bean.</p>
 *
 * <p>Identifiants : « F-0001 », « F-0002 »..., numérotés à partir du nombre de factures déjà en base au démarrage.
 * Suffisant pour un seul processus ; à plusieurs instances, on passerait par une séquence de la base.</p>
 */
public class FacturesJpa implements Factures {

    private final FactureJpaRepository jpa;
    private final AtomicLong dernierNumero;

    public FacturesJpa(FactureJpaRepository jpa) {
        this.jpa = jpa;
        this.dernierNumero = new AtomicLong(jpa.count());
    }

    @Override
    public void enregistrer(Facture facture) {
        jpa.save(FactureMapper.versEntite(facture));
    }

    @Override
    public Optional<Facture> parId(IdFacture id) {
        return jpa.findById(id.valeur()).map(FactureMapper::versDomaine);
    }

    @Override
    public Optional<Facture> parLocation(IdLocation idLocation) {
        return jpa.findByIdLocation(idLocation.valeur()).map(FactureMapper::versDomaine);
    }

    @Override
    public boolean existeUneImpayeePour(IdUsager idUsager) {
        return jpa.existsByIdUsagerAndStatut(idUsager.valeur(), StatutFacture.IMPAYEE.name());
    }

    @Override
    public IdFacture prochainIdentifiant() {
        return new IdFacture(String.format("F-%04d", dernierNumero.incrementAndGet()));
    }
}
