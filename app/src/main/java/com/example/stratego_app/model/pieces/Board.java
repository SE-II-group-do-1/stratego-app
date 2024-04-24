package com.example.stratego_app.model.pieces;

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

    public void setField(int y, int x, Piece piece){
        fields[y][x] = piece;
    }
    public Piece getField(int y, int x){ return fields[y][x];
    }
    public Piece[][] getBoard(){
        return fields;
    }

    public void setBoard(Board newBoard){
        Piece[][] newFields = newBoard.getBoard();
        for(int y=0; y<10;y++){
            for(int x=0; x<10; x++){
                if(newFields[y][x] != null) this.fields[y][x] = newFields[y][x];
            }
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

}
