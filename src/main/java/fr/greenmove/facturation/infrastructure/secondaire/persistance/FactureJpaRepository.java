package fr.greenmove.facturation.infrastructure.secondaire.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/**
 * Le repository Spring Data de la table {@code facture}. Technique : il parle d'entités et de chaînes, pas de
 * {@code Facture}. Seul l'adaptateur {@link FacturesJpa} s'en sert.
 */
public interface FactureJpaRepository extends JpaRepository<FactureEntite, String> {

    Optional<FactureEntite> findByIdLocation(String idLocation);

    boolean existsByIdUsagerAndStatut(String idUsager, String statut);
}
