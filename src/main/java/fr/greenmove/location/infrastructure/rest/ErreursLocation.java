package fr.greenmove.location.infrastructure.rest;

import fr.greenmove.location.domaine.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice(assignableTypes = LocationController.class)
public class ErreursLocation {

    @ExceptionHandler({VehiculeInconnu.class, UsagerInconnu.class, LocationInconnue.class})
    public ResponseEntity<Map<String, String>> introuvable(ExceptionMetier e) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("erreur", e.getMessage()));
    }

    @ExceptionHandler(ExceptionMetier.class)
    public ResponseEntity<Map<String, String>> conflit(ExceptionMetier e) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("erreur", e.getMessage()));
    }
}