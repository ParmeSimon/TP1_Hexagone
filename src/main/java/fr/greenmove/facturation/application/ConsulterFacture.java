package fr.greenmove.facturation.application;

import fr.greenmove.facturation.domaine.modele.IdFacture;
import fr.greenmove.facturation.domaine.modele.IdLocation;

import java.util.Optional;

/** Port entrant : lire une facture, par son identifiant ou par sa location. Vide = 404 côté REST. */
public interface ConsulterFacture {

    /** {@code GET /factures/{id}} */
    Optional<FactureVue> parId(IdFacture idFacture);

    /** {@code GET /factures?location={idLocation}} : vide tant que la facture n'a pas été émise. */
    Optional<FactureVue> parLocation(IdLocation idLocation);
}
