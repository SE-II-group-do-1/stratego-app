package com.example.stratego_app.model.Pieces;

public class Piece {
    private Rank rank;
    private boolean isVisible;
    private boolean isMovable;

    public Piece(Rank rank){
        this.rank = rank;
        this.isVisible = false;
        this.isMovable = rank != Rank.LAKE && rank != Rank.FLAG && rank != Rank.BOMB;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public boolean isMovable() {
        return isMovable;
    }
}
