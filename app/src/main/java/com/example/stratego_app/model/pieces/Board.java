package com.example.stratego_app.model.pieces;

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
            for(int y=0; y<10; y++){
                if(newFields[x][y] != null) this.fields[x][y] = newFields[x][y];
            }
        }
    }
}
