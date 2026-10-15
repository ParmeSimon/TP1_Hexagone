package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdLocation;

public class LocationInconnue extends ExceptionMetier{
    public LocationInconnue(IdLocation id){
        super("La location " +  id.valeur() + "est inconnue");
    }
}
