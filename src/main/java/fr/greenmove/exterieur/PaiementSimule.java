package fr.greenmove.exterieur;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Le prestataire de paiement, simulé : aucun appel réseau.
 *
 * <p>Il tient le rôle du client HTTP du prestataire (celui du M01, dont l'API change une à deux fois par an).
 * Il renvoie une référence de transaction, refuse les montants nuls ou négatifs, et rejette le <b>premier</b>
 * prélèvement des usagers payés par prélèvement (u3 dans {@code data/usagers.csv}) : la banque a dit non, le
 * second passera. C'est ce qui permet de jouer le scénario « facture impayée, puis régularisée ».</p>
 *
 * <p>À brancher derrière le port {@code Paiement} de l'hexagone Facturation : son domaine ne doit jamais voir
 * cette classe.</p>
 */
public class PaiementSimule {

    /** Levée quand le prestataire refuse le débit (à traduire en décision métier dans votre adaptateur). */
    public static class DebitRefuse extends RuntimeException {
        public DebitRefuse(String message) {
            super(message);
        }
    }

    private final Set<String> prelevementsRejetesUneFois;
    private final Map<String, Integer> tentatives = new HashMap<>();

    public PaiementSimule() {
        this(Set.of("u3"));
    }

    public PaiementSimule(Set<String> prelevementsRejetesUneFois) {
        this.prelevementsRejetesUneFois = Set.copyOf(prelevementsRejetesUneFois);
    }

    /** Débite l'usager et renvoie la référence de la transaction, ou lève {@link DebitRefuse}. */
    public synchronized String debiter(String idUsager, long montantCentimes) {
        if (montantCentimes <= 0) {
            throw new DebitRefuse("Montant à débiter invalide : " + montantCentimes);
        }
        int n = tentatives.merge(idUsager, 1, Integer::sum);
        if (prelevementsRejetesUneFois.contains(idUsager) && n == 1) {
            throw new DebitRefuse("Prélèvement rejeté par la banque de " + idUsager);
        }
        return "SIMU-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
