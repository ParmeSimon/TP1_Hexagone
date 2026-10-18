package fr.ekod.mediatheque;

/** Journal d'activité de l'accueil : conserve une ligne par message consigné. */
public interface Journal {

    void consigner(String message);
}
