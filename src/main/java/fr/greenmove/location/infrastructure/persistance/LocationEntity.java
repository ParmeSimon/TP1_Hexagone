package fr.greenmove.location.infrastructure.persistance;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "location")
public class LocationEntity {

    @Id
    private String id;
    private String usager;
    private String vehicule;
    private String type;
    private Instant debut;
    private Instant fin;
    private String statut;

    protected LocationEntity() { }

    public LocationEntity(String id, String usager, String vehicule, String type,
                          Instant debut, Instant fin, String statut) {
        this.id = id;
        this.usager = usager;
        this.vehicule = vehicule;
        this.type = type;
        this.debut = debut;
        this.fin = fin;
        this.statut = statut;
    }

    public String getId() { return id; }
    public String getUsager() { return usager; }
    public String getVehicule() { return vehicule; }
    public String getType() { return type; }
    public Instant getDebut() { return debut; }
    public Instant getFin() { return fin; }
    public String getStatut() { return statut; }
}