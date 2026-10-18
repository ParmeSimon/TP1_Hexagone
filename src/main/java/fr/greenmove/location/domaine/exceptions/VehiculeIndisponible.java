package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdVehicule;

public class VehiculeIndisponible extends ExceptionMetier{
    public VehiculeIndisponible(IdVehicule id){
        super("Le véhicule " +  id.valeur() + " est indisponible");
    }
}
