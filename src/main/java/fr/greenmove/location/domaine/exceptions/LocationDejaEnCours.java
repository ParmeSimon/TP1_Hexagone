package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdUsager;

public class LocationDejaEnCours extends ExceptionMetier{
    public LocationDejaEnCours(IdUsager id){
        super("L'usager " + id.valeur() + " a déjà une location en cours");
    }
}
