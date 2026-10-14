package fr.greenmove.exterieur;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Les données du service Flotte, chargées depuis {@code data/flotte.csv}.
 *
 * <p>Dans la vraie architecture, Flotte est un autre microservice interrogé en REST. Pour le TP, ces données
 * en mémoire suffisent. Cette classe n'implémente aucun port : c'est à l'hexagone Location de définir son port
 * {@code Flotte} dans son domaine, en vocabulaire métier, et d'écrire l'adaptateur qui l'enveloppe.</p>
 */
public class FlotteEnMemoire {

    /** Une ligne du fichier, telle quelle. Ce n'est pas un objet du domaine. */
    public record EntreeFlotte(String id, String type, boolean disponible, String motif) {}

    private final Map<String, EntreeFlotte> vehicules = new LinkedHashMap<>();

    public FlotteEnMemoire() {
        this("/data/flotte.csv");
    }

    public FlotteEnMemoire(String ressource) {
        try (var in = FlotteEnMemoire.class.getResourceAsStream(ressource)) {
            if (in == null) {
                throw new IllegalStateException("Ressource introuvable : " + ressource);
            }
            var lecteur = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            lecteur.readLine(); // en-tête
            String ligne;
            while ((ligne = lecteur.readLine()) != null) {
                if (ligne.isBlank()) {
                    continue;
                }
                String[] c = ligne.split(",", -1);
                vehicules.put(c[0], new EntreeFlotte(c[0], c[1], "oui".equals(c[2]), c[3]));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<EntreeFlotte> parId(String id) {
        return Optional.ofNullable(vehicules.get(id));
    }

    public Map<String, EntreeFlotte> tous() {
        return Map.copyOf(vehicules);
    }
}
