package com.example.stratego_app.model;


import android.content.Context;
import android.util.Log;

import com.example.stratego_app.model.pieces.*;
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
        Piece movingPiece = board.getField(startY, startX);


        if (validateMove(startX, startY, endX, endY)) {
            // Perform the move
            board.setField(endY, endX, movingPiece); // Move the piece to the new position
            board.setField(startY, startX, null); // Clear the original position

            return true; // Move was successful
        }

        return false;
    }
    //validateMove could just be deactivated with a cheat button
    private boolean validateMove(int startX, int startY, int endX, int endY) {
        return startX > startY && endX > endY;
    }
    @Override
    public void updateBoard(Board newBoard) {
        if (newBoard == null) {
            return;
        }
        board.setBoard(newBoard); //set entire board state
        notifyObservers(); //notify observers of board update
    }

    @Override
    public Piece getPieceAtPosition(int x, int y) {
        return board.getField(y, x);
    }

    public Board getBoard() {
        return board;
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
            board.setField(y, x, piece);
            placed = true;
        }
        if (placed) {
            notifyObservers();
        }
        return placed;
    }


    public void startGame() {
        if (gameSetupMode) {
            // additional setups and checks if needed
            gameSetupMode = false;
            notifyObservers();
        } else {
            Log.d("ModelService", "Attempted to start game without being in setup mode.");
        }
    }

    /**
     * serialize the board setup and save it to the storage at server
     * @param context
     */
    public boolean saveGameSetup(Context context) {
        FileOutputStream fos = null;
        JsonWriter writer = null;
        try {
            fos = context.openFileOutput("game_setup.json", Context.MODE_PRIVATE);
            writer = new JsonWriter(new OutputStreamWriter(fos, "UTF-8"));
            writer.setIndent("  ");
            writer.beginObject();
            for (int y = 0; y < board.getBoard().length; y++) {
                for (int x = 0; x < board.getBoard()[y].length; x++) {
                    Piece piece = board.getField(y, x);
                    if (piece != null) {
                        writer.name(x + "," + y).value(piece.getRank().toString());
                    }
                }
            }
            writer.endObject();
            return true; // saved
        } catch (Exception e) {
            Log.e("ModelService", "Error saving game setup", e);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                Log.e("ModelService", "Error closing streams", e);
                return false;
            }
        }
    }

    /**
     *
     * @return current state of the board
     */
    public Board getCurrentGameBoard() {
        return board;
    }



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


    /**
     * method to fill the board with pieces randomly
     */
    public void fillBoardRandomly() {
        List<Piece> pieces = generatePieces();
        board.fillBoardRandomly(pieces);
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



