package fr.greenmove.facturation.infrastructure.primaire.rest;

import fr.greenmove.facturation.application.ConsulterFacture;
import fr.greenmove.facturation.application.FactureVue;
import fr.greenmove.facturation.application.RegulariserFacture;
import fr.greenmove.facturation.domaine.exception.FactureDejaPayee;
import fr.greenmove.facturation.domaine.exception.FactureInconnue;
import fr.greenmove.facturation.domaine.modele.IdFacture;
import fr.greenmove.facturation.domaine.modele.IdLocation;
import fr.greenmove.facturation.domaine.exception.PaiementEncoreRefuse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * L'adaptateur primaire REST de Facturation, conforme à l'annexe A.
 *
 * <table>
 *   <tr><td>{@code GET /factures?location={id}}</td><td>200, ou 404 pas de facture pour cette location</td></tr>
 *   <tr><td>{@code GET /factures/{id}}</td><td>200, ou 404 facture inconnue</td></tr>
 *   <tr><td>{@code POST /factures/{id}/regularisation}</td><td>200 PAYEE, 404 inconnue, 409 déjà payée,
 *       502 paiement encore refusé</td></tr>
 * </table>
 *
 * <p>Il reçoit les <b>ports entrants</b> ({@link ConsulterFacture}, {@link RegulariserFacture}), jamais
 * {@code ServiceFacturation}. Son travail : traduire HTTP en appel de port (chaînes → identifiants du domaine) et
 * les exceptions métier en codes HTTP. Aucune règle métier ici.</p>
 *
 * <p>Les {@code @ExceptionHandler} sont dans le contrôleur (et pas dans un {@code @RestControllerAdvice} global) :
 * ils ne s'appliquent qu'aux routes de Facturation et n'interfèrent pas avec celles de Location.</p>
 */
@RestController
@RequestMapping("/factures")
public class FactureController {

    private final ConsulterFacture consulterFacture;
    private final RegulariserFacture regulariserFacture;

    public FactureController(ConsulterFacture consulterFacture, RegulariserFacture regulariserFacture) {
        this.consulterFacture = consulterFacture;
        this.regulariserFacture = regulariserFacture;
    }

    @GetMapping
    public ResponseEntity<FactureVue> parLocation(@RequestParam("location") String idLocation) {
        return ResponseEntity.of(consulterFacture.parLocation(new IdLocation(idLocation)));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FactureVue> parId(@PathVariable("id") String idFacture) {
        return ResponseEntity.of(consulterFacture.parId(new IdFacture(idFacture)));
    }

    @PostMapping("/{id}/regularisation")
    public FactureVue regulariser(@PathVariable("id") String idFacture) {
        return regulariserFacture.regulariser(new IdFacture(idFacture));
    }

    // ------------------------------------------------------------------ exceptions métier → codes HTTP

    @ExceptionHandler(FactureInconnue.class)
    ResponseEntity<Map<String, String>> inconnue(FactureInconnue e) {
        return erreur(HttpStatus.NOT_FOUND, e);
    }

    @ExceptionHandler(FactureDejaPayee.class)
    ResponseEntity<Map<String, String>> dejaPayee(FactureDejaPayee e) {
        return erreur(HttpStatus.CONFLICT, e);
    }

    @ExceptionHandler(PaiementEncoreRefuse.class)
    ResponseEntity<Map<String, String>> encoreRefuse(PaiementEncoreRefuse e) {
        return erreur(HttpStatus.BAD_GATEWAY, e);
    }

    /** Un identifiant vide (refusé par les value objects) est une requête mal formée. */
    @ExceptionHandler(IllegalArgumentException.class)
    ResponseEntity<Map<String, String>> requeteInvalide(IllegalArgumentException e) {
        return erreur(HttpStatus.BAD_REQUEST, e);
    }

    private static ResponseEntity<Map<String, String>> erreur(HttpStatus statut, RuntimeException e) {
        return ResponseEntity.status(statut).body(Map.of("erreur", e.getMessage()));
    }
}
