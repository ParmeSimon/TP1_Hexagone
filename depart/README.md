# Exo.2 – L'annuaire de la médiathèque : assertions et isolation

La médiathèque municipale veut une recherche d'adhérents pour l'accueil. Vous la construisez en TDD,
en binôme, en 40 minutes. L'objectif n'est pas seulement d'obtenir une classe qui marche, c'est de choisir
**le bon double de test pour chaque collaborateur** et d'écrire des assertions lisibles.

## Énoncé

`Annuaire.rechercher(saisie)` renvoie les adhérents dont le nom contient la saisie.

- Les adhérents viennent d'un `DepotAdherents` (interface fournie) : `List<Adherent> parNomContenant(String fragment)`.
- Chaque recherche est consignée dans un `Journal` (interface fournie) : `void consigner(String message)`.
- `Adherent` est un record fourni (`nom`, `prenom`).

### Règles

1. La saisie doit contenir au moins 2 caractères une fois les espaces de début et de fin retirés (`strip`),
   sinon `IllegalArgumentException("saisie trop courte")`. Une saisie `null` est traitée comme une saisie vide.
2. La recherche ignore la casse : le dépôt reçoit le fragment **en minuscules**, et l'implémentation en mémoire
   compare en minuscules.
3. Les résultats sont triés par nom, puis par prénom.
4. Il y a au plus 10 résultats.
5. S'il n'y a rien à renvoyer, la liste est vide (jamais `null`).
6. Chaque recherche acceptée est consignée avec le message `recherche : dupont, 2 résultats`.
   Le fragment est celui obtenu après `strip`, tel que saisi (la casse d'origine est conservée),
   et le nombre est celui **après** la limitation à 10. Une saisie refusée n'est pas consignée.

## Rappel sur les doubles

> **Stub ou fake pour ce que le code demande, mock pour ce qu'il fait.**

- Quand le code *demande* une information à un collaborateur (ici, la liste des adhérents),
  vous fournissez des réponses : un stub (réponses figées) ou un fake (petite implémentation qui se comporte
  comme le vrai).
- Quand le code *fait* quelque chose chez un collaborateur (ici, écrire dans le journal),
  la règle à tester est l'appel lui-même : un mock, que l'on vérifie.
- Une valeur comme `Adherent` ne se remplace jamais par un double : on en crée de vrais.

## Consignes

- Un cycle RED / GREEN / REFACTOR par comportement, un petit pas à la fois.
- Nommez vos tests en phrases qui disent la règle, par exemple `la_casse_est_ignoree()`.
- Utilisez AssertJ pour les listes (`containsExactly`, `hasSize`, `isEmpty`) et `assertThrows` pour les erreurs.
- La classe `Annuaire` n'existe pas : votre premier test rouge la crée.

## Livrable

1. Votre classe de test, dans `src/test/java/fr/ekod/mediatheque/`.
2. Votre classe `Annuaire`, dans `src/main/java/fr/ekod/mediatheque/`.
3. Un fichier `NOTES.md` à la racine, avec **trois lignes** sur le choix de vos doubles :
   quel double pour le dépôt et pourquoi, quel double pour le journal et pourquoi, et ce que vous
   n'avez volontairement pas doublé.

## Contenu du projet

```
pom.xml
src/main/java/fr/ekod/mediatheque/
    Adherent.java          (record fourni)
    DepotAdherents.java    (interface fournie)
    Journal.java           (interface fournie)
src/test/java/fr/ekod/mediatheque/
    .gitkeep               (à vous de jouer)
```

## Lancer les tests

```
mvn test
```

Prérequis : JDK 17+, Maven 3.9+.
