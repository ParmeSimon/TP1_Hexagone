package fr.greenmove.location.infrastructure.persistance;

import fr.greenmove.location.domaine.modele.*;

final class LocationMapper {

    private LocationMapper() { }

    static LocationEntity versEntite(Location l) {
        return new LocationEntity(
                l.id().valeur(),
                l.usager().valeur(),
                l.vehicule().valeur(),
                l.type().name(),
                l.debut(),
                l.fin(),
                l.statut().name());
    }

    static Location versDomaine(LocationEntity e) {
        return Location.reconstituer(
                new IdLocation(e.getId()),
                new IdUsager(e.getUsager()),
                new IdVehicule(e.getVehicule()),
                TypeVehicule.valueOf(e.getType()),
                e.getDebut(),
                e.getFin(),
                Statut.valueOf(e.getStatut()));
    }
}