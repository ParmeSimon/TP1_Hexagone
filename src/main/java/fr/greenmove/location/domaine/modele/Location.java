package fr.greenmove.location.domaine.modele;

import fr.greenmove.location.domaine.exceptions.*;

import java.time.Duration;
import java.time.Instant;

public class Location {

    private final IdLocation id;
    private final IdUsager usager;
    private final IdVehicule vehicule;
    private final TypeVehicule type;
    private final Instant debut;
    private Instant fin;
    private Statut statut;

    private Location(IdLocation id, IdUsager usager, IdVehicule vehicule, TypeVehicule type,
                     Instant debut, Instant fin, Statut statut) {
        this.id = id;
        this.usager = usager;
        this.vehicule = vehicule;
        this.type = type;
        this.debut = debut;
        this.fin = fin;
        this.statut = statut;
    }

    public static Location demarrer(IdLocation id, IdUsager usager, Vehicule vehicule, Instant debut){
        if(!vehicule.disponible()){
            throw new VehiculeIndisponible(vehicule.id());
        }
        return new Location(id,usager,vehicule.id(),vehicule.type(),debut,null,Statut.EN_COURS);
    }

    public static Location reconstituer(IdLocation id, IdUsager usager, IdVehicule vehicule, TypeVehicule type, Instant debut, Instant fin, Statut statut){
        return new Location(id, usager, vehicule, type, debut, fin, statut);
    }

    public void terminer (Instant fin){
        if(statut == Statut.TERMINEE){
            throw new LocationDejaTerminee(id);
        }
        if(fin.isBefore(debut)){
            throw new FinAvantDebut(id);
        }
        this.fin=fin;
        this.statut = Statut.TERMINEE;
    }

    public Long dureeMinutes(){
        if(fin==null){
            return null;
        }
        return Duration.between(debut,fin).toMinutes();
    }

    public boolean estEnCours() { return statut == Statut.EN_COURS; }

    public IdLocation id() { return id; }
    public IdUsager usager() { return usager; }
    public IdVehicule vehicule() { return vehicule; }
    public TypeVehicule type() { return type; }
    public Instant debut() { return debut; }
    public Instant fin() { return fin; }
    public Statut statut() { return statut; }

}
