package com.example.stratego_app.model;


import android.util.Log;

import com.example.stratego_app.model.pieces.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelService implements ModelServiceI{

    //implement singleton pattern
    private static ModelService instance;

    public static synchronized ModelService getInstance() {
        if (instance == null) {
            instance = new ModelService();
        }
        return instance;
    }

    private final Board board;
    private boolean gameSetupMode = true;
    private List<ObserverModelService> observers = new ArrayList<>();

    public ModelService() {
        this.board = new Board();
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

    @Override
    public void initializeGame() {
        this.gameSetupMode = true;
        // Assuming this is where the player starts placing their pieces
        // GUI integration
    }


    @Override
    public boolean movePiece(int startX, int startY, int endX, int endY) {



        if (validateMove(startX, startY, endX, endY)) {
            Piece movingPiece = board.getField(startX, startY);
            // Perform the move
            board.setField(endX, endY, movingPiece); // Move the piece to the new position
            board.setField(startX, startY, null); // Clear the original position

            return true; // Move was successful
        }

        return false;
    }
    //TODO validateMove could just be deactivated with a cheat button
    private boolean validateMove(int startX, int startY, int endX, int endY) {
        //Check if all Coordinates are on the board
        if (!areCoordinatesWithinBoardBounds(startX, startY, endX, endY)) {
            return false;
        }
        //Check if there is a piece
        if (board.getField(startX,startY) == null){
            return false;
        }
        //Initialize a moving Piece
        Piece movingPiece = board.getField(startX, startY);
        //TODO Check if it is your piece

        //Check if the Piece is allowed to move
        if (!movingPiece.isMovable()){
            return false;
        }

        // Check for a lake at the destination (null safe)
        Piece destinationPiece = board.getField(endX, endY);
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
            if (board.getField(currentX, currentY) != null) {
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
    public void updateBoard(Piece[][] newBoard) {
        //update the board from the server
    }
    @Override
    public Piece getPieceAtPosition(int x, int y) {
        return board.getField(x,y);
    }

    public Board getBoard() {
        return board;
    }

    /**
     * Place a piece on the board during the game setup.
     *
     * @param x     The x-coordinate (column) of the board.
     * @param y     The y-coordinate (row) of the board.
     * @param piece The piece to place on the board.
     * @return true if the piece was placed successfully, false otherwise.
     */
    public boolean placePieceAtGameSetUp(int x, int y, Piece piece) {
        boolean placed = false;
        if (gameSetupMode && y >= 6 && y <= 9) {
            board.setField(y, x, piece);
            placed = true;
        }
        if (placed) {
            notifyObservers();
        }
        return placed;

    }

    /*
    START managing lifecycle of game setup
     */
    public void startGame() {
        if (gameSetupMode) {
            // additional setups and checks if needed
            gameSetupMode = false;
            notifyObservers();
        } else {
            Log.d("ModelService", "Attempted to start game without being in setup mode.");
        }
    }
    public void saveGameSetup() {
        //Method implementieren
            }

    public Board getCurrentGameBoard() {
        return board;
    }

        /*
    END
     */



    /**
     * clear gameboard in settings editor
     */
    public void clearBoardExceptLakes() {
        for (int y = 0; y < board.getBoard().length; y++) {
            for (int x = 0; x < board.getBoard()[y].length; x++) {
                Piece piece = board.getField(y, x);
                if (piece != null && piece.getRank() != Rank.LAKE) {
                    board.setField(y, x, null);
                }
            }
        }
        notifyObservers();
    }


    // ---------------- methods to fill Board randomly in settings Fragment  ----------------------------------------

    //----- method to set Board ----



    public void fillBoardRandomly() {
        List<Piece> pieces = generatePieces();
        board.fillBoardRandomly(pieces);
        notifyObservers();
    }


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



