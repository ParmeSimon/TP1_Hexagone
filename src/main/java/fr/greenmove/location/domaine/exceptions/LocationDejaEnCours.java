package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdUsager;

public class LocationDejaEnCours extends ExceptionMetier{
    public LocationDejaEnCours(IdUsager id){
        super("L'utilisateur a déja une location en cours : " +  id.valeur());
    }
}
