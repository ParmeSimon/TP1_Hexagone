package fr.greenmove.location.domaine.exceptions;

import fr.greenmove.location.domaine.modele.IdUsager;

public class FactureImpayee extends ExceptionMetier{
    public FactureImpayee(IdUsager id){
        super("L'usager " + id.valeur() + " a une facture impayée");
    }
}
