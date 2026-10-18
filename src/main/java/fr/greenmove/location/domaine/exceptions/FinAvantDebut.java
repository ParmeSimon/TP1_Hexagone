package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdLocation;

public class FinAvantDebut extends ExceptionMetier{
    public FinAvantDebut(IdLocation id){
        super("La fin de la location " + id.valeur() + " précède son début");
    }
}
