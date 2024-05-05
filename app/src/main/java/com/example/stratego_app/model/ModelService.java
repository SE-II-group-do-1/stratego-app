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
    //TODO validateMove could just be deactivated with a cheat button
    private boolean validateMove(int startX, int startY, int endX, int endY) {
        //Check if all Coordinates are on the board
        if (!areCoordinatesWithinBoardBounds(startX, startY, endX, endY)) {
            return false;
        }
        //Initialize a moving Piece
        Piece movingPiece = board.getField(startY, startX);
        //TODO Check if it is your piece

        //Check if the Piece is allowed to move
        if (!movingPiece.isMovable()){
            return false;
        }

        //Check for a Lake
        if (board.getField(endY, endX).getRank() == Rank.LAKE){ return false;}

        //Check for diagonal move
        if (isMoveDiagonal(startX, startY, endX, endY)){
            return false;
        }

        //Check Step-Size
        if (!checkStepSize(movingPiece, startX,endX,startY,endY)){
            return false;
        }

        //Check if the field is empty
        if(board.getField(endY,endX) == null) {
            return true;
        }
        //Check if other Piece is an opponent
        Piece otherPiece = board.getField(endY,endX);
        if (otherPiece.getColor() != movingPiece.getColor()){
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
            if (board.getField(currentY, currentX) != null) {
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
        return null;
    }

    public Board getBoard() {
        return board;
    }
}
