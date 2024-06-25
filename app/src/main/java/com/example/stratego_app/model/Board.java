package com.example.stratego_app.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {

    private Piece[][] fields;

    public Board(){
        this.fields =  new Piece[10][10];
        // set lakes
        fields[4][2] = new Piece(Rank.LAKE);
        fields[4][3] = new Piece(Rank.LAKE);
        fields[5][2] = new Piece(Rank.LAKE);
        fields[5][3] = new Piece(Rank.LAKE);

        fields[4][6] = new Piece(Rank.LAKE);
        fields[4][7] = new Piece(Rank.LAKE);
        fields[5][6] = new Piece(Rank.LAKE);
        fields[5][7] = new Piece(Rank.LAKE);
    }

    public void setField(int x, int y, Piece piece){
        fields[x][y] = piece;
    }
    public Piece getField(int x, int y){ return fields[x][y];
    }
    public Piece[][] getBoard(){
        return fields;
    }

    public void setBoard(Board newBoard){
        Piece[][] newFields = newBoard.getBoard();
        for(int x=0; x<10;x++){
            System.arraycopy(newFields[x], 0, this.fields[x], 0, 10);
        }
    }



    /**
     * fills board with all pieces randomly placed on the gameboard
     */
    public void fillBoardRandomly(List<Piece> pieces) {
        List<Integer> positions = new ArrayList<>();
        // Only consider positions in the lower half of the board (rows 6 to 9)
        for (int i = 60; i < 100; i++) {
            if (getField(i / 10, i % 10) == null || getField(i / 10, i % 10).getRank() != Rank.LAKE) {
                positions.add(i);
            }
        }
        Collections.shuffle(positions);

        for (int i = 0; i < pieces.size(); i++) {
            if (i >= positions.size()) break; // Check if there are not enough positions for all pieces
            int pos = positions.get(i);
            setField(pos / 10, pos % 10, pieces.get(i));
        }
    }

    /**
     * checks if placed pieces is in a valid location without a set piece on it
     * @param y y-coordinates
     * @param x x-coordinates
     * @return true it the field is empty
     */

    public boolean isValidLocation(int y, int x) {
        if (y < 0 || y >= 10 || x < 0 || x >= 10) {
            return false;
        }
        Piece existingPiece = getField(y, x);
        if (existingPiece != null && existingPiece.getRank() == Rank.LAKE) {
            return false;
        }

        return true;
    }


    public void rotateBoard() {
        int rows = this.fields.length;
        int cols = this.fields[0].length;

        // Reverse the rows
        for (int i = 0; i < rows / 2; i++) {
            Piece[] temp = fields[i];
            fields[i] = fields[rows - 1 - i];
            fields[rows - 1 - i] = temp;
        }
        // Reverse the columns
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols / 2; j++) {
                Piece temp = fields[i][j];
                fields[i][j] = fields[i][cols - 1 - j];
                fields[i][cols - 1 - j] = temp;
            }
        }

    }

}
