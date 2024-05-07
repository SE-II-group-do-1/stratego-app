package com.example.stratego_app.model;


import android.content.Context;
import android.util.Log;

import com.google.gson.stream.JsonWriter;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelService implements ModelServiceI{

    //implement singleton pattern
    private static ModelService instance;
    String tag = "ModelService";

    public static synchronized ModelService getInstance() {
        if (instance == null) {
            instance = new ModelService();
        }
        return instance;
    }

    private GameState currentGameState = GameState.WAITING;  // default state

    private Board gameBoard;
    private Board setupBoard;
    private boolean gameSetupMode = true;
    private Player currentPlayer;
    private List<ObserverModelService> observers = new ArrayList<>();

    public ModelService() {
        this.gameBoard = new Board();
        this.setupBoard = new Board();
    }

/*
START observer methods to notify e.g. gameboardview when changes arise
 */
    public void addObserver(ObserverModelService observer) {
        observers.add(observer);
    }

    public void removeObserver(ObserverModelService observer) {
        observers.remove(observer);
    }

    protected void notifyObservers() {
        for (ObserverModelService observer : observers) {
            observer.onBoardUpdated();
        }
    }

    /*
    END observer methods
     */

    /*
    START gameState transitions
     */
    public void setGameState(GameState newState) {
        this.currentGameState = newState;
        notifyObservers();  // Notify UI components of the state change
    }

    public GameState getGameState() {
        return this.currentGameState;
    }

    /*
    END gameState transitions
     */

    /*
    START Player management
     */

    /**
     * Creates a new player or updates the existing player with the provided username and ID.
     * This method ensures that only one Player instance exists within the application.
     * If player exists, username and ID will be updated. If no player exists, new player will be
     * instantiated with the given username and the ID assigned by the server.
     *
     * @param username the username of the player.
     * @param id the unique ID assigned by the server to the player.
     */
    public void createOrUpdatePlayer(String username, int id) {
        if (currentPlayer == null) {
            currentPlayer = new Player(username);
        }
        currentPlayer.setId(id);  //id is provided by server
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }
    /*
    END
     */

    @Override
    public void initializeGame() {
        if (gameSetupMode) {
            this.setupBoard = new Board();
        } else {
            this.gameBoard = new Board();
        }
    }


    public void startGame() {
        if (gameSetupMode) {
            // transferSetupToGameBoard(); method? or how do we transfer the set-up to the final board?
            gameSetupMode = false;
            setGameState(GameState.INGAME);
            notifyObservers();
        } else {
            Log.d(tag, "Attempted to start game without being in setup mode.");
        }
    }


    @Override
    public boolean movePiece(int startX, int startY, int endX, int endY) {
        Piece movingPiece = gameBoard.getField(startY, startX);


        if (validateMove(startX, startY, endX, endY)) {
            Piece movingPiece = gameBoard.getField(startX, startY);
            // Perform the move
            gameBoard.setField(endY, endX, movingPiece); // Move the piece to the new position
            gameBoard.setField(startY, startX, null); // Clear the original position

            return true; // Move was successful
        }

        return false;
    }
    private boolean validateMove(int startX, int startY, int endX, int endY) {
        //Check if all Coordinates are on the board
        if (!areCoordinatesWithinBoardBounds(startX, startY, endX, endY)) {
            return false;
        }
        //Check if there is a piece
        if (gameBoard.getField(startX,startY) == null){
            return false;
        }
        //Initialize a moving Piece
        Piece movingPiece = gameBoard.getField(startX, startY);
        //TODO Check if it is your piece

        //Check if the Piece is allowed to move
        if (!movingPiece.isMovable()){
            return false;
        }

        // Check for a lake at the destination (null safe)
        Piece destinationPiece = gameBoard.getField(endX, endY);
        if (destinationPiece != null && destinationPiece.getRank() == Rank.LAKE) {
            return false;
        }

        //Check for diagonal move
        if (isMoveDiagonal(startX, startY, endX, endY)){
            return false;
        }

        //Check Step-Size
        if (!checkStepSize(movingPiece, startX,endX,startY,endY)){
            return false;
        }

        //Check if the field is empty
        if(destinationPiece == null) {
            return true;
        }
        /* Check if other Piece is an opponent */
        if (destinationPiece.getColor() == movingPiece.getColor()){
                return false;
        }
            return true;
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

    private boolean isMoveDiagonal(int startX, int startY, int endX, int endY) {
        return startX != endX && startY != endY;
    }

    private boolean areCoordinatesWithinBoardBounds(int startX, int startY, int endX, int endY) {
        return startX >= 0 && startX <= 9 && startY >= 0 && startY <= 9 &&
                endX >= 0 && endX <= 9 && endY >= 0 && endY <= 9;
    }
    @Override
    public void updateBoard(Board newBoard) {
        if (newBoard == null) {
            return;
        }
        gameBoard.setBoard(newBoard); //set entire board state
        notifyObservers(); //notify observers of board update
    }
    @Override
    public Piece getPieceAtPosition(int x, int y) {
        return gameBoard.getField(y, x);
    }

    public Board getGameBoard() {
        return gameBoard;
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
        if (gameSetupMode && y >= 6 && y <= 9) {
            setupBoard.setField(y, x, piece);
            placed = true;
        }
        if (placed) {
            notifyObservers();
        }
        return placed;
    }



    /**
     * serialize the board setup and save it to the storage at server
     * @param context
     */
    public boolean saveGameSetup(Context context) {
        FileOutputStream fileOutStream = null;
        JsonWriter writer = null;
        try {
            fileOutStream = context.openFileOutput("game_setup.json", Context.MODE_PRIVATE);
            writer = new JsonWriter(new OutputStreamWriter(fileOutStream, "UTF-8"));
            writer.setIndent("  ");
            writer.beginObject();
            for (int y = 0; y < gameBoard.getBoard().length; y++) {
                for (int x = 0; x < gameBoard.getBoard()[y].length; x++) {
                    Piece piece = gameBoard.getField(y, x);
                    if (piece != null) {
                        writer.name(x + "," + y).value(piece.getRank().toString());
                    }
                }
            }
            writer.endObject();
            return true; // saved
        } catch (Exception e) {
            Log.e(tag, "Error saving game setup", e);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fileOutStream != null) {
                    fileOutStream.close();
                }
            } catch (IOException e) {
                Log.e(tag, "Error closing streams", e);
            }
        }
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
        notifyObservers();
    }


    /**
     * method to fill the board with pieces randomly
     */
    public void fillBoardRandomly() {
        List<Piece> pieces = generatePieces();
        gameBoard.fillBoardRandomly(pieces);
        notifyObservers();
    }


    /**
     * generates a list of pieces including number of occurrence, rank, color and id)
     * @return piece
     */
    private List<Piece> generatePieces() {
        List<Piece> pieces = new ArrayList<>();
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.FLAG, null, 1)));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.MARSHAL, null, 2)));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.GENERAL, null, 3)));
        pieces.addAll(Collections.nCopies(2, new Piece(Rank.COLONEL, null, 4)));
        pieces.addAll(Collections.nCopies(3, new Piece(Rank.MAJOR, null, 5)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.CAPTAIN, null, 6)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.LIEUTENANT, null, 7)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.SERGEANT, null, 8)));
        pieces.addAll(Collections.nCopies(5, new Piece(Rank.MINER, null, 9)));
        pieces.addAll(Collections.nCopies(8, new Piece(Rank.SCOUT, null, 10)));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.SPY, null, 11)));
        pieces.addAll(Collections.nCopies(6, new Piece(Rank.BOMB, null, 12)));

        return pieces;
    }

}



