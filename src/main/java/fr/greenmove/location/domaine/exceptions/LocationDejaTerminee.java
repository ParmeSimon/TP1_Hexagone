package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdLocation;

public class LocationDejaTerminee extends ExceptionMetier {
    public LocationDejaTerminee(IdLocation id){
        super("La location " +  id.valeur() + "est déja terminé");
    }
}
