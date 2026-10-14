package fr.greenmove.location.infrastructure.rest;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Vérifie que le déployable démarre : {@code GET /ping}.
 *
 * <p>Votre adaptateur REST ({@code LocationController}, contrat en annexe A de l'énoncé) vient à côté.
 * Il dépend des ports entrants, jamais de l'implémentation des cas d'usage.</p>
 */
@RestController
public class PingController {

    @GetMapping("/ping")
    public String ping() {
        return "GreenMove : Location et Facturation démarrés, à vous de jouer.";
    }
}
