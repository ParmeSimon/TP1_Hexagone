package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdVehicule;

public class VehiculeInconnu extends ExceptionMetier {
    public VehiculeInconnu(IdVehicule id){
        super("Le véhicule " +  id.valeur() + " est inconnue");
    }
}
