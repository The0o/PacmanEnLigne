# PacmanEnLigne

- Théo LAIDIN 
- Nicolas MARIE-CATHERINE 
- Minh THOAI

## Lancer une partie

1. Se mettre dans la branche "pacmanServeurClient".
2. Lancer le jeu `src/TestEnLigne`.
3. Se connecter ou créer un compte.
4. Choisir le mode solo ou multijoueur
5. Appuyer sur commencer.

PS: Si le mode multijoueur est sélectionné, le server Pacman attendra un nouveau, tant qu'un autre joueur ne s'est pas connecté, la partie ne se lancera pas.

Le server Pacman et le server Web tournent actuellement sur un server privé avec une addresse ip dédiée donc pas besoin de les lancer.


## Lancer une partie en local

1. Dans `src/vue/GameLauncher` modifier :

+ à la ligne 68 :
```bash
#Mettre en false

private static final boolean USE_ONLINE_SERVER = false;
```
+ à la ligne 1144 :
```bash
#Mettre l'ip du server en "localhost"

 String ipServeur = "localhost";
```

+ à la ligne 1173 :
```bash
#Mettre "localhost"

 String ipServeur = "localhost";
```

2. Dans `src/vue/GameLauncherEnLigne` modifier :

+ à la ligne 24  :
```bash
#Mettre ip server en localhost
String ipServeur = "localhost"
```

3. Dans `src\serveurPacman\ConnectionToClient` modifier :

+ à la ligne 24  :
```bash
#utiliser l'ip du serveur DEFAULT_SCORE_API_URL "localhost"
String ipServeur = ""http://localhost:8080//test/api/scores"
```
Ensuite,

1. Lancer le Server Web via l'autre projet Github
2. Lancer le Server Pacman via `serveurPacman/LaunchServer`
3. Lancer Lancer le jeu `src/TestEnLigne`.

# Pistes d'améliorations

- Ajout d'un système de boutique en ligne de skins 
- Faire en sorte que le server Pacman choisisse lui même le layout de jeu.
- Ajout d'un mode "versus" 1vs1 Pacman contre Pacman ( score )
- Ajout d'un mode "versus" où un des joueurs incarne le fantôme.

## Bugs / réglages à corriger

- La difficulté ( les fantômes sont trop forts ).
- le système de room personalisée, problème lorsque l'on clique sur le bouton "commencer" au lieu du bouton "rejoindre"

