package fr.greenmove.location.infrastructure.persistance;

import fr.greenmove.location.domaine.modele.*;
import fr.greenmove.location.domaine.ports.Locations;

import java.util.Optional;

public class LocationsJpa implements Locations {

    private final LocationJpaRepository jpa;

    public LocationsJpa(LocationJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public IdLocation prochainId() {
        return new IdLocation(String.format("L-%04d", jpa.count() + 1));
    }

    @Override
    public void sauvegarder(Location location) {
        jpa.save(LocationMapper.versEntite(location));
    }

    @Override
    public Optional<Location> parId(IdLocation id) {
        return jpa.findById(id.valeur()).map(LocationMapper::versDomaine);
    }

    @Override
    public boolean aUneLocationEnCours(IdUsager usager) {
        return jpa.existsByUsagerAndStatut(usager.valeur(), Statut.EN_COURS.name());
    }
}