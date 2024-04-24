package com.example.stratego_app.model.pieces;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Board {
    public static final String TAG = "GameBoardView";
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

    /**
     * fills board with all pieces randomly placed on the gameboard
     */
    public void fillBoardRandomly(List<Piece> pieces) {
        List<Integer> positions = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            if (getField(i / 10, i % 10) == null || getField(i / 10, i % 10).getRank() != Rank.LAKE) {
                positions.add(i);
            }
        }
        Collections.shuffle(positions);

        for (int i = 0; i < pieces.size(); i++) {
            int pos = positions.get(i);
            setField(pos / 10, pos % 10, pieces.get(i));
        }
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
     * checks if the piece is dropped at a valid location
     * @param y
     * @param x
     * @return
     */

    public boolean isValidLocation(int y, int x) {
        if (y < 0 || y >= 10 || x < 0 || x >= 10) {
            Log.d(TAG, "Invalid location: Out of board bounds.");
            return false;
        }
        Piece existingPiece = getField(y, x);
        if (existingPiece != null && existingPiece.getRank() == Rank.LAKE) {
            Log.d(TAG, "Invalid location: Lake at position (" + y + ", " + x + ").");
            return false;
        }

        Log.d(TAG, "Valid location for placement at (" + y + ", " + x + ").");
        return true;
    }


}
