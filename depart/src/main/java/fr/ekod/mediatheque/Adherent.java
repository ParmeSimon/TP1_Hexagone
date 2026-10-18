package fr.ekod.mediatheque;

/** Un adhérent de la médiathèque, identifié ici par son nom et son prénom. */
public record Adherent(String nom, String prenom) {

    @Override
    public String toString() {
        return prenom + " " + nom;
    }
}
