package com.example.stratego_app.model;

import com.example.stratego_app.model.Pieces.*;

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
        Piece movingPiece = board.getField(startY, startX);
        if (movingPiece == null || !movingPiece.isMovable()) return false;

        if (endX < 0 || endY < 0 || endX >= 10 || endY >= 10) {
            return false; // Move is out of bounds
        }
        Piece destinationPiece = board.getField(endY, endX);
        if (destinationPiece != null && destinationPiece.getRank() != Rank.LAKE) {
            return false; // Destination is blocked by another piece that cannot be captured
        }

        return false;
    }

    @Override
    public void updateBoard(Piece[][] newBoard) {

    }

    @Override
    public Piece getPieceAtPosition(int x, int y) {
        return null;
    }
}
