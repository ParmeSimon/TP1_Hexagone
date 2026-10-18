package fr.ekod.mediatheque;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Annuaire {

    private final DepotAdherents depot;
    private final Journal journal;

    public Annuaire(DepotAdherents depot, Journal journal) {
        this.depot = depot;
        this.journal = journal;
    }

    public List<Adherent> rechercher(String saisie) {
        if (saisie == null || saisie.strip().length() < 2) {
            throw new IllegalArgumentException("saisie trop courte");
        }

        String saisieStrip = saisie.strip();
        String fragmentMinscule = saisieStrip.toLowerCase();

        List<Adherent> brut = depot.parNomContenant(fragmentMinscule);
        if (brut == null) {
            brut = List.of();
        }

        List<Adherent> resultats = brut.stream()
                .sorted(Comparator.comparing(Adherent::nom, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(Adherent::prenom, String.CASE_INSENSITIVE_ORDER))
                .limit(10)
                .collect(Collectors.toList());

        journal.consigner("recherche : " + saisieStrip + ", " + resultats.size() + " résultats");

        return resultats;
    }
}