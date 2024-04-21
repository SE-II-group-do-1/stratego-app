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
        //Check if all Coordinates are on the board
        if (startX < 0 || startX > 9 || startY < 0 || startY > 9 ||
                endX < 0 || endX > 9 || endY < 0 || endY > 9) {
            // Any of the coordinates is outside the board bounds
            return false;
        }

        //Initialize a moving Piece
        Piece movingPiece = board.getField(startY, startX);
        //Check if it is your piece

        //Check if the Piece is allowed to move
        if (!movingPiece.isMovable()){
            return false;
        }
        //Check if the Piece is allowed to make this move(Scout?)
        //Check if the field is empty
        if(board.getField(endY,endX) == null) {
            return true;
        }
        //Check if other piece is a Lake
        //Check if other Piece is an opponent

        Piece otherPiece = board.getField(endY,endX);
        //Starting a Fight
        return true;
        }
    //Fight after validate or before?
    public boolean fight(Piece moving, Piece other){
        moving.getRank();
        other.getRank();
        return true;
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
