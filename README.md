# Bot "Thermostat" de la Team-Radiateur
## Présentation
Ce projet a pour but de créer un bot Discord pour le serveur de la Team-Radiateur. Il est développé en Java avec l'API [JDA](https://jda.wiki/).
La finalité est d'avoir un bot qui puisse, via le système de réflection de Java, charger automatiquement les commandes et les événements, et ce, sans avoir à modifier le code source du bot.

Ce projet sert principalement à apprendre le fonctionnement de l'API de réflexion, l'utiliser, et adapter au maximum le code pour simplifier l'ajout de nouvelles fonctionnalités.

## Avancée du projet
- [x] Configuration automatique du bot
- [ ] Création de la base de données
  - [ ] Création des tables
    - [ ] `bad_words`
- [ ] Commandes
  - [x] Musique
    - [x] Play/Pause
    - [x] Skip
    - [x] Now Playing
    - [x] Clear (only admin)
    - [x] Lyrics
    - [x] Playlist
  - [x] Help
  - [x] Ping
  - [ ] Mots interdits (requiert la base de données)
    - [ ] Ajout
    - [ ] Retrait
    - [ ] Listing
  - [ ] Annonces
  - [ ] Suppression de tous les messages d'une personne
  - [ ] Suppression de x messages d'un channel
  - [x] Goélette au rapport
  - [ ] Météo Belgique
  - [ ] Infos générales du serveur
- [ ] Événements
  - [x] Channel
    - [x] Création
    - [x] Édition
    - [x] Suppression
  - [x] Message
    - [x] Création
    - [x] Suppression
    - [x] Édition
  - [x] Rôle
    - [x] Création
    - [x] Modification
    - [x] Suppression
    - [x] Attribution
    - [x] Retrait
  - [ ] Membre
    - [ ] Arrivée
    - [ ] Départ
    - [ ] Bannissement
    - [ ] Pardon (unban)
  - [ ] Emojis
    - [ ] Création
    - [ ] Édition
    - [ ] Suppression
  - [x] Interactions (commandes) → détection et renvoi vers les commandes
  - [x] Bot prêt
  - [x] Changement de status voix
