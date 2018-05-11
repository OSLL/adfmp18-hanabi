# adfmp18-hanabi

## [Use Case](https://docs.google.com/document/d/1q9iMniDn_P9BYeRMWgg-3JFajvIzjrDRyh-nXqjIYyw/edit)

## UI analysis

### Open the rules

**1** click from main menu and game activities

### Start a new single player game

**2** clicks and **1** swipe from the main menu activity = **3** actions in total

### Join a multiplayer game

**1-2** clicks to open the list of games (1 for WIFI p2p and 2 for internet) + **2** clicks to join + **0-1** input fields to enter (depends on whever this game is locked or not) = **3-5** actions in total

### Start a new multiplayer game

**2-3** clicks to open dialogue (2 for WIFI p2p and 3 for internet) + **1** swipe to specify the max number of players + **0-1** text input feilds to feel (depends on whever one wants the game to be locked) + **1** click to create game = **4-6** actions in total

### What can be improved

Firstly, settings and rules activities should be accessible from game room (where players wait for room to be full to start the game), not just from main menu and game activities.

Secondly, the game screen looks terrible. We should use some graphical libraries (like LibGDX) which allow to easily make beutiful animations.

Thirdly, we should add an ability to see what the other people know about their hands. We also should add a game events log.

Fourthly, PDF for rules is bad, we should make it as a text with pictures.

## Battery waste

```
==== just sitting in main menu

05-11 00:17:27.315 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 27%, rawlevel is:: 27
05-11 00:28:34.920 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 24%, rawlevel is:: 24

==== 2 players

05-11 00:28:40.101 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 24%, rawlevel is:: 24
05-11 00:32:11.747 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 21%, rawlevel is:: 21

==== 2 players

05-11 00:35:17.405 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 21%, rawlevel is:: 21
05-11 00:39:29.741 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 19%, rawlevel is:: 19

==== 2 players

05-11 00:40:20.940 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 19%, rawlevel is:: 19
05-11 00:43:51.952 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 17%, rawlevel is:: 17

==== 3 players

05-11 00:53:24.660 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 18%, rawlevel is:: 18
05-11 00:58:10.161 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 16%, rawlevel is:: 16

==== 3 players

05-11 01:02:43.150 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 15%, rawlevel is:: 15
05-11 01:05:56.249 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 13%, rawlevel is:: 13

==== 3 players

05-11 01:21:10.129 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 17%, rawlevel is:: 17
05-11 01:23:56.607 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 14%, rawlevel is:: 14


==== 5 players

05-11 01:37:48.370 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 19%, rawlevel is:: 19
05-11 01:42:38.866 3860-3860/ru.mit.spbau.hanabi D/Battery level: Battery Level in % is:: 18%, rawlevel is:: 18
```
