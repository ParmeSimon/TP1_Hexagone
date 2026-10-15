package fr.greenmove.location.infrastructure.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

public interface LocationJpaRepository extends JpaRepository<LocationEntity, String> {

    boolean existsByUsagerAndStatut(String usager, String statut);
}