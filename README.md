# Stratego

## Dev Setup 
#### Use of emulator and second device(connected via USB)

1. Google Chrome am PC öffnen 
2. enter chrome://inspect 
![start](https://github.com/SE-II-group-do-1/stratego-app/assets/161967717/6a307156-17a7-4df5-a78e-58cd95ec7b06)

3. click "Port Forwarding"
   Port: 53216
   IP adresse and port: localhost:53216
![portForwardingSettings](https://github.com/SE-II-group-do-1/stratego-app/assets/161967717/a43e315d-c812-4649-bf0f-00986c8e983f)

4. click "Configure"
   IP adresse and port: localhost:53216
![targetDiscoverySettings](https://github.com/SE-II-group-do-1/stratego-app/assets/161967717/7ec8def8-ef67-4ac0-af28-617cad5f7237)

5. Open Android Studio and both on the emulator as well as your second device GoogleChrome should be opened
6. enter localhost:53216
7. When both devices are connected your PC GoogleChrome page should look like this
![EndScreen](https://github.com/SE-II-group-do-1/stratego-app/assets/161967717/bd7e8599-7af3-4028-9e33-f83029cadfe9)

ATTENTION! Do not close the Google Chrome Pages while operating! 

#### Start Server
1. Open IntelliJ and start server repository
2. run StrategoApplication.java



## Project Details
#### Description
Stratego is a two player game where each player has a 40 piece army. The first player to enter the lobby is assigned the blue color. Before the start of the game, players arrange their 40 pieces in a 4×10 configuration at either end of the board. 
One player plays the red colored army, and the other plays the blue army.The aim of the game is to capture the opponent's flag. The player with the red pieces begins the first move. Afterwards players move alternatively. 

#### How to Play
step-by-step instructions on how to start a game, how the game interface works
- run main branch.
- The starting interface advices you to enter a username and enter the lobby to wait for an opponent to play against
- use the settings editor to set your gameboard manually or use "fill board" to randomly fill your gameboard
- with two players beeing in the lobby and the boards set, a game can be started by clicking on the button "Start Game"

#### Rules
Detailed explanation of the rules as described in the game description:
- Starting the game
- Movement and attack rules
- Special abilities of each piece

You can either move your own piece or attack your opponent, whereby both can only be carried out in one move by the "scout". The higher ranked piece always captures the lower, except when stated otherwise. When a piece attacks another piece with equal rank, both are removed. Each army includes a Flag, 6 Bombs, a Spy, and the remaining pieces are those which represent different army ranks, such as Marshal, General, Colonel, Major, and so on, down to Scouts. 

Rank: B     (6 x) Bomb (Immovable; any piece attacking a Bomb is removed from the game, unless the attacking piece was a Miner 

Rank: 10    (1 x) Marshal (Most powerful piece, but vulnerable to capture by an attacking Spy)

Rank: 9     (1 x) General 

Rank: 8     (2 x) Colonel

Rank: 7     (3 x) Major

Rank: 6     (4 x) Captain

Rank: 5     (4 x) Lieutenant

Rank: 4     (4 x) Sergeant

Rank: 3     (5 x) Mineur (Can defuse (i. e. capture) Bombs)

Rank: 2     (8 x) scout (Can move any distance in a horizontal or vertical straight line without leaping over pieces or lakes)

Rank: 1     (1 x) Spy (Weakest piece, captured by any other attacking piece, but an attacking Spy can capture the Marshal)

Rank: F     (1 x) flag (it is immobile and can be captured by any piece. If you capture it, you are the winner)


##### On your turn: 
Players alternate turns. The Red Player takes the first turn. Each turn, you must do ONE of the following
- MOVE one of your pieces or
- ATTACK with one of your pieces
You cannot move and attack in the same turn. If none of your pieces are able to move or attack, the game is over and your opponent wins.

##### Rules for Moving: 
- Only one piece can be moved per turn, and it can move only one square per turn (except Scouts, see “Specialist Pieces” on Page 5). Pieces can move forward, backward or sideways, but never diagonally.
- Two pieces cannot occupy the same square at the same time.
- Pieces cannot jump over or move through an occupied square.
- The Lakes areas in the center of the board contain no squares. Pieces must move around these areas, never through or over them.
- Once a piece has been moved to a square and the player’s hand removed, it cannot be moved back to its original position.
- Pieces cannot be moved back and forth between the same two squares in three consecutive turns. The player who started this must stop first.
NOTE: The Flag and the Bombs cannot move. These pieces must remain where they were placed at the beginning of the game.

##### Rules for Attacking: 
- If one of your opponent’s pieces occupies a square in front, beside or behind yours, you can attack it. You cannot attack diagonally. Attacking is always optional. Scouts have special attack abilities!
- To attack, pick up your piece and place it on the field of your opponent’s piece. 
     If your piece’s rank is HIGHER than your opponent’s, you win the attack and capture his or her piece. Your winning piece now occupies that square.
     If your piece’s rank is LOWER than your opponent’s, you lose the attack and your piece is captured. Your opponent’s piece stays in its square.
     If your piece’s rank MATCHES the rank of your opponent’s, both pieces are captured.
Captured pieces are immediately removed from the board.
- When a piece attacks a Bomb:
    The attacking piece loses and is captured. The Bomb remains in its square.
    Only a Miner can attack and remove a bomb! See “Specialist Pieces” on Page 5.
NOTE: The Flag and the Bombs cannot attack. They can only wait for the opposing player to attack them.

##### Game Win: 
- A player attacks and captures his/her opponent’s flag. The attacking player is the winner.
- A player cannot move a piece or attack. The opposing player is the winner. 
