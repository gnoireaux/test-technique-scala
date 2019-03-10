
# Test technique Scala
Voici le repo git du test technique Ubilab en Scala.

* Créer un fork de ce repo
* Cloner le repo sur votre ordinateur, puis ``sbt``

Les tests: 
* Step 1 : initialisation du projet avec 0 et 1 resultat
* Step 2 : Après l'ajout d'un résultat
* Step 3 : après l'ajout de 3 resultats
* Step 4 : après l'ajout de 3 resultats (en se basant sur des événements)


L'objectif et de faire passer tous les tests unitaires 1 à 4 dans l'ordre.
* Chaque étape doit être une branche git crée et chaque test unitaire implémenté un nouveau commit.
* Vous pouvez modifier comme vous le souhaitez les fichiers, tant que les tests unitaires passent. (c'est même conseillé :D)
* Nous ne voulons pas que vous ajoutiez de bibliothèques pour vous aider dans ce projet (pour les 4 premières étapes)
    
[Bonus] Si vous le voulez vous pouvez créer une api REST JSON avec les bibliothèques de votre choix.

Chez Ubilab nous aimons :
* le code simple lisible et élégant
* le code maintenable,
* le code testable,

Ayez toujours à l'esprit que c'est principalement ces critères que nous jugerons.

## Build

Run `sbt compile` to build the project.

## Running unit tests

Run `sbt test` to execute the unit tests.
