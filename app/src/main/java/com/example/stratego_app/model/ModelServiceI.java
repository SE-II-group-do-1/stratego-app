package com.example.stratego_app.model;

public interface ModelServiceI {

    /**
     * Attempts to move a piece from one location to another.
     * This method should handle checking if the move is legal,
     * updating the local board state, and communicating the move to the server.
     *
     * @param startX the starting X-coordinate of the piece
     * @param startY the starting Y-coordinate of the piece
     * @param endX the ending X-coordinate after the move
     * @param endY the ending Y-coordinate after the move
     * @return true if the move was successful, false otherwise
     */
    boolean movePiece(int startX, int startY, int endX, int endY);

    /**
     * Updates the local board state based on a new configuration,
     * usually received from the server after a move has been validated.
     *
     * @param newBoard a 2D array of Piece objects representing the new board state
     */
    void updateBoard(Board newBoard);

}
