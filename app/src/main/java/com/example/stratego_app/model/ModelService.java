package com.example.stratego_app.model;

import com.example.stratego_app.model.pieces.*;

public class ModelService implements ModelServiceI{
    private Board board;

    public ModelService() {
        this.board = new Board();
    }
    @Override
    public void initializeGame() {
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
}
