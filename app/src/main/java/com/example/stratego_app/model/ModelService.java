package com.example.stratego_app.model;


import com.example.stratego_app.model.pieces.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ModelService implements ModelServiceI{
    private Board board;
    private boolean gameSetupMode = true;

    public ModelService() {
        this.board = new Board();
    }
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
    public void updateBoard(Piece[][] newBoard) {
        //update the board from the server
    }

    @Override
    public Piece getPieceAtPosition(int x, int y) {
        return null;
    }

    public Board getBoard() {
        return board;
    }


    /**
     * methods needed for game set-up
     */
    public boolean placePiece(int x, int y, Piece piece) {
        if (gameSetupMode) { // Only allow placing pieces in setup mode
            board.setField(y, x, piece);
            return true;
        }
        return false;
    }
    public void startGame() {
        this.gameSetupMode = false;
    }

    // ---------------- methods to fill Board randomly in settings Fragment  ----------------------------------------

    //----- method to set Board ----
    public void setPiecesOnGameBoard()  {
        fillBoardRandomly();
    }

    private void fillBoardRandomly() {
        List<Piece> pieces = generatePieces();
        board.fillBoardRandomly(pieces);
    }


    private List<Piece> generatePieces() {
        List<Piece> pieces = new ArrayList<>();

        pieces.add(new Piece(Rank.FLAG, null, 1));
        pieces.add(new Piece(Rank.MARSHAL, null, 2));
        pieces.add(new Piece(Rank.GENERAL, null, 3));
        pieces.addAll(Collections.nCopies(2, new Piece(Rank.COLONEL, null, 4)));
        pieces.addAll(Collections.nCopies(3, new Piece(Rank.MAJOR, null, 5)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.CAPTAIN, null, 6)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.LIEUTENANT, null, 7)));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.SERGEANT, null, 8)));
        pieces.addAll(Collections.nCopies(5, new Piece(Rank.MINER, null, 9)));
        pieces.addAll(Collections.nCopies(8, new Piece(Rank.SCOUT, null, 10)));
        pieces.add(new Piece(Rank.SPY, null, 11));
        pieces.addAll(Collections.nCopies(6, new Piece(Rank.BOMB, null, 12)));

        return pieces;
    }

}



