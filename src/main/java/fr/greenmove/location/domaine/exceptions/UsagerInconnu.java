package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdUsager;

public class UsagerInconnu extends ExceptionMetier{
    public UsagerInconnu(IdUsager id){
        super("L'usager " + id.valeur() + " est inconnu");
    }
}
