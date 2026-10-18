# GreenMove – TP1 : deux hexagones, Location et Facturation (Java 17+, Spring Boot 3, Maven, H2 en mémoire)

- **Compiler** : `mvn -q compile`
- **Lancer les tests** (domaines, cas d'usage, adaptateurs, 7 règles ArchUnit, scénario de l'annexe C) : `mvn test`
- **Démarrer** : `mvn spring-boot:run` (port 8080, `GET /ping` pour vérifier)
- **Scénario de l'annexe C, automatique** : `mvn test -Dtest=ScenarioBoutEnBoutTest`
- **Scénario de l'annexe C, à la main** (appli démarrée ; sous PowerShell, écrire `curl.exe`) :
  `curl -s -X POST localhost:8080/locations -H "Content-Type: application/json" -d "{\"usager\":\"u3\",\"vehicule\":\"T117\"}"`
  puis `curl -s -X POST localhost:8080/locations/L-0001/fin`, `curl -s "localhost:8080/factures?location=L-0001"` (IMPAYEE),
  un nouveau `POST /locations` pour u3 (409), `curl -s -X POST localhost:8080/factures/F-0001/regularisation` (PAYEE), et à nouveau `POST /locations` (201).
- **Dossier** (diagramme, ADR, retour d'expérience) et sources du diagramme : dossier `docs/`.
