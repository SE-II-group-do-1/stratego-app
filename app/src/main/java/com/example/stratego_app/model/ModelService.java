package com.example.stratego_app.model;

import com.example.stratego_app.connection.LobbyClient;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/*TODO:
 - code cleanup
 */
public class ModelService implements ModelServiceI{
    private static ModelService instance;
    private GameState currentGameState;
    private Board gameBoard;
    private Player currentPlayer;
    private Player currentOpponent;
    private Color playerColor;
    private boolean currentTurn;

    private static List<ObserverModelService> listeners;

    public static synchronized ModelService getInstance() {
        if (instance == null) {
            instance = new ModelService();
        }
        return instance;
    }

    public ModelService() {
        this.gameBoard = new Board();
        this.currentGameState = GameState.WAITING;
        this.currentTurn = false;
    }

    @Override
    public void updateBoard(Board newBoard) {
        if (newBoard == null) {
            return;
        }
        gameBoard.setBoard(newBoard); //set entire board state
        //TODO: notify UI
    }

    //commit update from server
    public void updateBoard(int y, int x, Piece piece){
        if(currentGameState != GameState.INGAME) return;
        gameBoard.setField(x,y,piece);
        currentTurn = !currentTurn;
        notifyUI();
    }

    //send update to Server
    public void requestUpdate(int y, int x, Piece piece){
        LobbyClient.getInstance().sendUpdate(y,x,piece, currentPlayer);
    }

    public static void subscribe(ObserverModelService o){
        listeners.add(o);
    }

    public static void unsubscribe(ObserverModelService o){
        listeners.remove(o);
    }

    public static void notifyUI(){
        listeners.forEach(ObserverModelService::update);
    }



    @Override
    public boolean movePiece(int startX, int startY, int endX, int endY) {
        Piece movingPiece;


        if (validateMove(startX, startY, endX, endY)) {
            movingPiece = gameBoard.getField(startX, startY);
            // Perform the move
            gameBoard.setField(endY, endX, movingPiece); // Move the piece to the new position
            gameBoard.setField(startY, startX, null); // Clear the original position

            return true; // Move was successful
        }

        return false;
    }
    private boolean validateMove(int startX, int startY, int endX, int endY) {

        Piece movingPiece = gameBoard.getField(startX, startY);
        boolean notMyPiece = movingPiece.getColor() != playerColor;
        boolean isPieceMovable= movingPiece.isMovable();
        boolean isMoveDiagonal = startX != endX && startY != endY;;
        boolean areCoordinatesWithinBounds = startX >= 0 && startX <= 9 && startY >= 0 && startY <= 9 &&
                endX >= 0 && endX <= 9 && endY >= 0 && endY <= 9;

        /*
        - is it my piece?
        - is it movable?
        - coords within bounds?
        - move diagonal?
        - destination valid -> not lake, own piece
        - check stepsize
         */
        if (!areCoordinatesWithinBounds || isMoveDiagonal || !isPieceMovable || notMyPiece || !checkStepSize(movingPiece, startX,endX,startY,endY)) {
            return false;
        }

        // Check destination: is it friendly (own) lake?
        Piece destinationPiece = gameBoard.getField(endX, endY);
        boolean isDestLake = destinationPiece.getRank() == Rank.LAKE;
        boolean isDestFriend = destinationPiece.getColor() == playerColor;

        return !isDestLake && !isDestFriend;

    }

    private boolean checkStepSize(Piece movingPiece, int startX, int endX, int startY, int endY) {
        // Check if the piece is a Scout
        if (movingPiece.getRank() == Rank.SCOUT) {


            // Iterate over all intermediate spaces between the start and end points
            if (!iterateOverAllIntermediateSpaces(startX,endX,startY,endY)){
                return false;
            }

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

    private boolean iterateOverAllIntermediateSpaces(int startX, int endX, int startY, int endY) {
        // Determine the direction of movement (horizontal or vertical)
        boolean isHorizontal = startX == endX;
        boolean isVertical = startY == endY;

        // Calculate the step size and intermediate coordinates
        int step = isHorizontal ? Integer.signum(endY - startY) : Integer.signum(endX - startX);
        int currentX = startX;
        int currentY = startY;

        while ((isHorizontal && currentY != endY) || (isVertical && currentX != endX)) {
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

    @Override
    public Piece getPieceAtPosition(int x, int y) {
        return gameBoard.getField(y, x);
    }

    public Board getGameBoard() {
        return gameBoard;
    }

    public Color getPlayerColor(){
        return playerColor;
    }

    public void setPlayerColor(Color color){
        playerColor = color;
    }

    public void setGameState(GameState newState) {
        if (this.currentGameState != newState) {
            this.currentGameState = newState;
        }
    }

    public GameState getGameState() {
        return this.currentGameState;
    }

    public void Player(String username, int id) {
        currentPlayer = new Player(username, id);
    }
    public void Player(Player player){
        currentPlayer = player;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public String getPlayerName(){
        return currentPlayer.getUsername();
    }

    public void Opponent(Player opponent) {
        currentOpponent = opponent;
    }

    public String getOpponentName(){
        return currentOpponent.getUsername();
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
        if (currentGameState==GameState.SETUP && y >= 6 && y <= 9) {
            gameBoard.setField(y, x, piece);
            placed = true;
        }
        if (placed) {
            //TODO: notify UI
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
        //TODO: notify UI
    }


    /**
     * method to fill the board with pieces randomly
     */
    public void fillBoardRandomly() {
        List<Piece> pieces = generatePieces();
        gameBoard.fillBoardRandomly(pieces);
        //TODO: notify UI
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
}