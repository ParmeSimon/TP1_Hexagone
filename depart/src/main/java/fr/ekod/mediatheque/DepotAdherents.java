package fr.ekod.mediatheque;

import java.util.List;

/** Source des adhérents : renvoie ceux dont le nom contient le fragment, sans ordre garanti. */
public interface DepotAdherents {

    List<Adherent> parNomContenant(String fragment);
}
