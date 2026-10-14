# GreenMove – Location et Facturation, deux hexagones : code de départ du TP1

EADL-BC01FM03 – Architecture hexagonale. En binômes, rendu le dimanche 25 octobre 2026 à 23h59 sur Campus Connect.

## Démarrer

Java 17 à 25 et Maven suffisent. Aucune base à installer : H2 tourne en mémoire.

```bash
mvn -q test            # les tests du code de départ passent : ArchUnit et démarrage du contexte
mvn spring-boot:run    # puis http://localhost:8080/ping
```

Dans l'IDE : ouvrir le `pom.xml` comme projet Maven, exécuter `GreenMoveApplication`.

## Ce que contient le code de départ

```
fr.greenmove
├── GreenMoveApplication.java              un seul main, un seul déployable, deux hexagones
├── exterieur/                             les systèmes extérieurs, simulés (n'appartiennent à aucun hexagone)
│   ├── FlotteEnMemoire.java               les véhicules de data/flotte.csv
│   ├── UsagersEnMemoire.java              les usagers de data/usagers.csv (abonné ou non, moyen de paiement)
│   ├── PaiementSimule.java                le prestataire : rejette le premier prélèvement de u3, accepte le second
│   └── ConfigurationExterieur.java        les trois beans
├── location/                              HEXAGONE 1
│   ├── domaine/                           Location, ses règles, ses identifiants, ses ports sortants : À ÉCRIRE
│   │   ├── Flotte.java, Vehicule, IdVehicule, TypeVehicule   FOURNIS EN EXEMPLE : un port sortant et ses types
│   │   └── evenements/LocationTerminee.java   FOURNI : l'événement publié, le contrat avec Facturation
│   ├── application/                       VIDE : DemarrerLocation, TerminerLocation, ConsulterLocation, ServiceLocation
│   └── infrastructure/
│       ├── rest/PingController.java       GET /ping, à côté de votre futur LocationController
│       ├── flotte/FlotteSurSimulation.java   FOURNI EN EXEMPLE : l'adaptateur du port Flotte (implémente, reçoit, traduit)
│       ├── persistance/                   VIDE : entité JPA, repository, adaptateur du port Locations
│       ├── evenements/                    VIDE : adaptateur du port PublicationEvenements sur le bus Spring
│       ├── impayes/                       VIDE : adaptateur du port Impayes, qui appelle ConsulterImpayes
│       └── config/ConfigurationLocation.java   le câblage : le bean Flotte est écrit, les autres sont à faire pareil
└── facturation/                           HEXAGONE 2
    ├── domaine/                           VIDE : Facture, Montant, GrilleTarifaire, ses ports sortants
    ├── application/                       EmettreFacture, ConsulterFacture, RegulariserFacture, ServiceFacturation
    │   └── ConsulterImpayes.java          FOURNI : le port entrant que Location appelle, le second contrat
    └── infrastructure/
        ├── rest/                          VIDE : FactureController
        ├── persistance/                   VIDE : le dépôt des factures (en mémoire ou JPA, table facture)
        ├── evenements/                    VIDE : EcouteurLocationTerminee (@EventListener → EmettreFacture)
        └── config/ConfigurationFacturation.java   le câblage, à compléter
src/test/java/fr/greenmove
├── ArchitectureTest.java                  7 règles ArchUnit : domaine sans technique, jamais le domaine du voisin
├── location/infrastructure/flotte/FlotteSurSimulationTest.java   FOURNI EN EXEMPLE : le test d'un adaptateur, sans Spring
├── GreenMoveApplicationTest.java          le contexte Spring démarre
└── ScenarioBoutEnBoutTest.java            le scénario de l'annexe C, désactivé : à activer quand tout est câblé
```

## L'exemple fourni : le port Flotte, de bout en bout

Un port sortant et son adaptateur sont écrits, pour que vous fassiez les autres sur le même modèle :

| Pièce | Fichier | Rôle |
|---|---|---|
| Le port | `location/domaine/Flotte.java` | une interface dans le domaine, en vocabulaire métier, avec les types du domaine (`Vehicule`, `IdVehicule`) |
| L'adaptateur | `location/infrastructure/flotte/FlotteSurSimulation.java` | implémente le port, reçoit `FlotteEnMemoire` par constructeur, traduit `EntreeFlotte` en `Vehicule` |
| Le câblage | `location/infrastructure/config/ConfigurationLocation.java` | `@Bean Flotte flotte(FlotteEnMemoire simulation)` : l'adaptateur est déclaré sous le type de son port |
| Le test | `src/test/.../FlotteSurSimulationTest.java` | l'adaptateur construit à la main, sans Spring |

Le cas d'usage recevra `Flotte` par constructeur (l'interface, jamais `FlotteSurSimulation`) et, dans ses tests, une lambda
ou une `Map` à la place. Pour `Paiement`, l'adaptateur a une traduction de plus : attraper `PaiementSimule.DebitRefuse` et la
rendre au domaine comme un résultat métier, pas comme une exception technique.

## Comment les deux hexagones se parlent

| Sens | Quoi | Comment |
|---|---|---|
| Location → Facturation | l'événement `LocationTerminee` | port `PublicationEvenements` → adaptateur `ApplicationEventPublisher` → `@EventListener` dans `facturation.infrastructure.evenements` → port entrant `EmettreFacture` |
| Location → Facturation | « cet usager a-t-il une facture impayée ? » | port `Impayes` (domaine de Location) → adaptateur `ImpayesParFacturation` → port entrant `ConsulterImpayes` (fourni) |

Ce qu'un hexagone a le droit d'importer de l'autre, et seulement depuis son `infrastructure` : ses ports entrants
(`application`) et ses événements publiés (`domaine.evenements`). Rien d'autre. `ArchitectureTest` le vérifie.

## L'API attendue

Contrat complet en annexe A de l'énoncé.

| Opération | Requête | Réponse | Erreurs |
|---|---|---|---|
| Démarrer | `POST /locations` `{ "usager": "u1", "vehicule": "V428" }` | 201, statut `EN_COURS` | 404 véhicule ou usager inconnu, 409 véhicule indisponible, usager déjà en location, usager avec une facture impayée |
| Terminer | `POST /locations/{id}/fin` | 200, fin, durée en minutes, statut `TERMINEE` | 404, 409 déjà terminée |
| Consulter | `GET /locations/{id}` | 200 | 404 |
| Facture d'une location | `GET /factures?location={id}` | 200, montant en centimes, statut `PAYEE` ou `IMPAYEE`, référence | 404 pas encore de facture |
| Consulter une facture | `GET /factures/{id}` | 200 | 404 |
| Régulariser | `POST /factures/{id}/regularisation` | 200, statut `PAYEE`, référence | 404, 409 déjà payée, 502 paiement encore refusé |

## Les règles métier (celles du M01, dans Facturation)

| Règle | Valeur |
|---|---|
| Prise en charge (vélo et trottinette) | 1,00 € |
| Prix à la minute – vélo | 0,15 € |
| Prix à la minute – trottinette | 0,22 € |
| Plafond journalier | 15,00 € |
| Réduction abonné | 20 %, appliquée après le plafond |
| Durée (Location) | minutes entières, arrondies à la minute inférieure, fin antérieure au début = erreur |
| Une seule location en cours par usager (Location) | sinon refus |
| Usager avec une facture impayée (Location demande à Facturation) | refus |
| Une facture par location (Facturation) | émettre deux fois = la même facture |

Vérifications : 24 min de vélo, non abonné → 460 centimes. 18 min de trottinette, abonné → 397 centimes.
5 h de vélo, non abonné → 1 500 centimes, abonné → 1 200 centimes. 0 minute → 100 centimes (80 pour un abonné).

## Par où commencer

1. Lisez l'exemple fourni (port `Flotte`, adaptateur, bean, test), puis `location.domaine` : `Location`, ses règles, ses exceptions, ses tests. Sans Spring.
2. `location.application` : les ports entrants et `ServiceLocation`, testé avec des doubles écrits à la main
   (un `Impayes` qui dit toujours non, une horloge fixe, une `Map` pour `Locations`).
3. `facturation.domaine` : `Montant`, `GrilleTarifaire` et ses trois cas, `Facture`.
4. `facturation.application` : `ServiceFacturation`, testé avec des doubles (un `Paiement` qui refuse).
5. Les adaptateurs des deux côtés, le câblage, les contrôleurs REST.
6. La communication en dernier : l'écouteur, l'adaptateur `Impayes`, puis activez `ScenarioBoutEnBoutTest`.

Le critère qui ne trompe pas : quand les tests des deux domaines et des cas d'usage tournent en quelques millisecondes
sans rien démarrer, les hexagones tiennent. Tant qu'ils ont besoin de Spring ou de H2, ils ne tiennent pas.

## Autre stack

La stack est libre : même API, mêmes règles, même séparation en deux hexagones, un bus interne ou l'équivalent.
Dites-le en une ligne au début du dossier et indiquez comment lancer le projet et les tests.
