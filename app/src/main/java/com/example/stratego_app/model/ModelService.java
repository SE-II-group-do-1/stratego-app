package com.example.stratego_app.model;


//import android.util.Log;
import com.example.stratego_app.connection.LobbyClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelService implements ModelServiceI{
    private static final String TAG = "modelservice";
    private static ModelService instance;
    private GameState currentGameState;
    private Board gameBoard;
    private Player currentPlayer;
    private Player currentOpponent;
    private Color playerColor;
    private boolean currentTurn;

    private static List<ObserverModelService> listeners = new ArrayList<>();

    private Position oldPos; //Previous position of last changed piece, opponent and self
    private Position newPos; //New position of last changed piece

    public static synchronized ModelService getInstance() {
        if (instance == null) {
            instance = new ModelService();
        }
        return instance;
    }

    public ModelService() {
        this.gameBoard = new Board();
        this.currentGameState = GameState.WAITING;
        this.oldPos = new Position(-1,-1);
        this.newPos = new Position(-1,-1);
    }

    //only for committing data from server
    @Override
    public void updateBoard(Board newBoard) {
        if (newBoard == null) {
            return;
        }
        // blue version of board is always right side up (server version)
        if(playerColor == Color.RED){
            newBoard.rotateBoard();
        }
        gameBoard.setBoard(newBoard); //set entire board state
        this.currentTurn = true;
        notifyUI();
    }

    public static void subscribe(ObserverModelService o){
        listeners.add(o);
    }

    public static void unsubscribe(ObserverModelService o){
        listeners.remove(o);
    }

    public static void notifyUI(){
        //Log.d(TAG, "notifyUI() called, notifying observers.");
        listeners.forEach(ObserverModelService::update);
    }

    public static void notifyClient(Board copyForServer){
        //blue version of board is right way up. if red player -> turn board for server
        if(instance.currentTurn){
            LobbyClient.sendUpdate(checkForRotation(copyForServer));
            instance.currentTurn = false;
        }
    }

    public void checkWin(Color winner) {
        if(winner == null) return;
        if(winner == playerColor){
            //Log.i("ModelService", "win");
            setGameState(GameState.WIN);
        } else {
            setGameState(GameState.LOSE);
            //Log.i("ModelService", "lose");
        }
    }

    public static Board checkForRotation(Board copy){
        if(getInstance().playerColor == Color.BLUE) {
            return copy;
        } else {
            copy.rotateBoard();
            return copy;
        }
    }

    public static Position checkForRotationPos(Position pos){
        Position ret = new Position(0,0);
        if(instance.playerColor == Color.RED){
            ret.setX(9 - pos.x); //9 because length of fields in board
            ret.setY(9 - pos.y);
            return ret;
        }
        return ret;
    }


    /**
     * validates move, if valid -> updates Board -> sends request via LobbyClient, notifies UI
     * @param startX the starting X-coordinate of the piece
     * @param startY the starting Y-coordinate of the piece
     * @param endX the ending X-coordinate after the move
     * @param endY the ending Y-coordinate after the move
     * @return
     */
    @Override
    public boolean movePiece(int startX, int startY, int endX, int endY) {
        Piece movingPiece;
        if (validateMove(startX, startY, endX, endY)) {
            movingPiece = gameBoard.getField(startX, startY);
            //create copy on which to perform move -> send copy as request to server. only server updates actual board
            Board copyForRequestToServer = new Board();
            copyForRequestToServer.setBoard(gameBoard);
            copyForRequestToServer.setField(endX, endY, movingPiece);
            copyForRequestToServer.setField(startX, startY, null);

            oldPos.setX(startX);
            oldPos.setY(startY);
            newPos.setX(endX);
            newPos.setY(endY);

            notifyClient(copyForRequestToServer);
            notifyUI();
            
            return true;
        }
        return false;
    }
    public boolean validateMove(int startX, int startY, int endX, int endY) {

        Piece movingPiece = gameBoard.getField(startX, startY);
        boolean notMyPiece = movingPiece.getColor() != playerColor;
        boolean isPieceMovable= movingPiece.isMovable();
        boolean isMoveDiagonal = startX != endX && startY != endY;
        boolean areCoordinatesWithinBounds = startX >= 0 && startX <= 9 && startY >= 0 && startY <= 9 &&
                endX >= 0 && endX <= 9 && endY >= 0 && endY <= 9;

        if (!areCoordinatesWithinBounds || notMyPiece|| isMoveDiagonal || !isPieceMovable || !checkStepSize(movingPiece, startX,endX,startY,endY)) {
            return false;
        }

        Piece destinationPiece = gameBoard.getField(endX, endY);
        if(destinationPiece == null) return true;

        boolean isDestLake = destinationPiece.getRank() == Rank.LAKE;
        boolean isDestFriend = destinationPiece.getColor() == playerColor;

        return !isDestLake && !isDestFriend;

    }

    private boolean checkStepSize(Piece movingPiece, int startX, int endX, int startY, int endY) {
        // Check if the piece is a Scout
        if (movingPiece.getRank() == Rank.SCOUT) {
            // Iterate over all intermediate spaces between the start and end points
            return iterateOverAllIntermediateSpaces(startX, endX, startY, endY);
        } else {
            // For non-Scout pieces, check if the move exceeds the maximum step size
            int distanceX = Math.abs(endX - startX);
            int distanceY = Math.abs(endY - startY);
            if (distanceX > 1 || distanceY > 1) {
                return false; // Move exceeds maximum step size
            }
        }
        return true;
    }

    /*public boolean isValidMove(int startX, int startY, int endX, int endY) {
        Piece movingPiece = gameBoard.getField(startX, startY);
        if (movingPiece == null || movingPiece.getColor() != playerColor) return false;

        return validateMove(startX, startY, endX, endY) && checkStepSize(movingPiece, startX, endX, startY, endY);
    }*/



    public boolean iterateOverAllIntermediateSpaces(int startX, int endX, int startY, int endY) {
        // Determine the direction of movement (horizontal or vertical)
        boolean isHorizontal = startX == endX;
        boolean isVertical = startY == endY;

        // Calculate the step size and intermediate coordinates
        int step = isHorizontal ? Integer.signum(endY - startY) : Integer.signum(endX - startX);
        int currentX = startX;
        int currentY = startY;

        while ((isHorizontal && currentY != endY-step) || (isVertical && currentX != endX-step)) {
            // Move to the next intermediate space
            if (isHorizontal) {
                currentY += step;
            } else {
                currentX += step;
            }
            // Check if the intermediate space is empty
            if (gameBoard.getField(currentX, currentY) != null) {
                return false; // There is a piece in the way, invalid move for Scout
            }
        }
        return true;
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public Color getPlayerColor(){
        return playerColor;
    }

    public void setPlayerColor(Color color){
        playerColor = color;
        this.currentTurn = playerColor == Color.RED;
        if(gameBoard != null){
            for(int i=0; i<gameBoard.getBoard().length; i++){
                for(int j=0; j<gameBoard.getBoard()[0].length; j++){
                    if(gameBoard.getField(i,j) != null){
                        gameBoard.getField(i,j).setColor(playerColor);
                    }
                }
            }
        }
        notifyUI();
    }

    public void setGameState(GameState newState) {
        if (this.currentGameState != newState) {
            //Log.d(TAG, "Setting game state from " + currentGameState + " to " + newState);
            this.currentGameState = newState;
            notifyUI();
        } else {
            //Log.d(TAG, "Attempt to set game state to current state: " + newState);
        }
    }

    public GameState getGameState() {
        return this.currentGameState;
    }

    public void Player(Player player){
        currentPlayer = player;
        notifyUI();
    }

    public void Opponent(Player opponent) {
        currentOpponent = opponent;
        notifyUI();
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public Player getCurrentOpponent() {
        return currentOpponent;
    }

    public Position getOldPos() {
        return oldPos;
    }

    public void setOldPos(Position oldPos) {
        this.oldPos = oldPos;
    }

    public Position getNewPos() {
        return newPos;
    }

    public void setNewPos(Position newPos) {
        this.newPos = newPos;
    }

    /**
     * place a piece on the board during the game setup.
     *
     * @param x     The x-coordinate (column) of the board.
     * @param y     The y-coordinate (row) of the board.
     * @param piece The piece to place on the board.
     * @return true if the piece was placed successfully, false otherwise.
     */
    public boolean placePieceAtGameSetUp(int x, int y, Piece piece) {
        boolean placed = false;
        // currentGameState==GameState.SETUP && <- condition extraced from if below
        if ( y >= 6 && y <= 9) {
            gameBoard.setField(y, x, piece);
            placed = true;
        }
        if (placed) {
            notifyUI();
        }
        return placed;
    }

    /**
     * clear gameboard in settings editor
     */
    public void clearBoardExceptLakes() {
        for (int y = 0; y < gameBoard.getBoard().length; y++) {
            for (int x = 0; x < gameBoard.getBoard()[y].length; x++) {
                Piece piece = gameBoard.getField(y, x);
                if (piece != null && piece.getRank() != Rank.LAKE) {
                    gameBoard.setField(y, x, null);
                }
            }
        }
        notifyUI();
    }


    /**
     * method to fill the board with pieces randomly
     */
    public void fillBoardRandomly() {
        List<Piece> pieces = generatePieces();
        gameBoard.fillBoardRandomly(pieces);
        notifyUI();
    }


    /**
     * generates a list of pieces including number of occurrence, rank, color and id)
     * @return piece
     */
    private List<Piece> generatePieces() {
        List<Piece> pieces = new ArrayList<>();
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.FLAG, playerColor)));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.MARSHAL, playerColor)));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.GENERAL, playerColor)));
        pieces.addAll(Collections.nCopies(2, new Piece(Rank.COLONEL, playerColor)));
        pieces.addAll(Collections.nCopies(3, new Piece(Rank.MAJOR, playerColor)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.CAPTAIN, playerColor)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.LIEUTENANT, playerColor)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.SERGEANT, playerColor)));
        pieces.addAll(Collections.nCopies(5, new Piece(Rank.MINER, playerColor)));
        pieces.addAll(Collections.nCopies(8, new Piece(Rank.SCOUT, playerColor)));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.SPY, playerColor)));
        pieces.addAll(Collections.nCopies(6, new Piece(Rank.BOMB, playerColor)));

        return pieces;
    }

    public boolean isSetupComplete() {
        for (int y = 6; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (gameBoard.getField(y, x) == null) {
                    return false;
                }
            }
        }
        return true;
    }

    public void leaveGame() {
        if(currentGameState == GameState.INGAME){
            LobbyClient.leaveLobby(currentPlayer.getId());
            newInstance();
        }
    }

    public boolean isCurrentTurn(){
        return this.currentTurn;
    }

    public void newInstance(){
        ModelService.instance = new ModelService();
    }
}