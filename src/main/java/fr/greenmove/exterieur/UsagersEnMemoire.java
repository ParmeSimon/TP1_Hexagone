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
 * Les données du service Compte usager, chargées depuis {@code data/usagers.csv}.
 *
 * <p>Deux hexagones en ont besoin, pour deux questions différentes : Location demande si l'usager existe,
 * Facturation demande s'il est abonné. Chacun définit son propre port ({@code Usagers} d'un côté,
 * {@code Abonnements} de l'autre) et son propre adaptateur par-dessus cette classe. Aucun des deux ne partage
 * le port de l'autre.</p>
 */
public class UsagersEnMemoire {

    public record EntreeUsager(String id, boolean abonne, String moyenPaiement) {}

    private final Map<String, EntreeUsager> usagers = new LinkedHashMap<>();

    public UsagersEnMemoire() {
        this("/data/usagers.csv");
    }

    public UsagersEnMemoire(String ressource) {
        try (var in = UsagersEnMemoire.class.getResourceAsStream(ressource)) {
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
                usagers.put(c[0], new EntreeUsager(c[0], "oui".equals(c[1]), c[2]));
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public Optional<EntreeUsager> parId(String id) {
        return Optional.ofNullable(usagers.get(id));
    }
}
