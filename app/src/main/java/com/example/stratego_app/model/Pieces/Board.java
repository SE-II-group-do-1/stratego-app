package com.example.stratego_app.model.Pieces;

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
}
