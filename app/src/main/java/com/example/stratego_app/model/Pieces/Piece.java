package com.example.stratego_app.model.Pieces;

public class Piece {
    private Rank rank;
    private boolean isVisible;
    private boolean isMovable;
    private Color color; // Adding color to distinguish between players' pieces
    private int id; // Optional: useful for tracking specific pieces in complex logic

    //normal Constructor
    public Piece(Rank rank, Color color, int id) {
        this.rank = rank;
        this.color = color;
        this.id = id;
        this.isVisible = false;
        this.isMovable = rank != Rank.LAKE && rank != Rank.FLAG && rank != Rank.BOMB;
    }
    //Constructor for lakes
    public Piece(Rank rank) {
        this.rank = rank;
        this.isVisible = true; // Lakes are always visible
        this.isMovable = false; // Lakes do not move
    }

    public Rank getRank() {
        return rank;
    }
    public int getId() {
        return id;
    }
    public Color getColor(){
        return color;
    }

    public boolean isVisible() {
        return isVisible;
    }
    public void setVisible(boolean visible){
        this.isVisible = visible;
    }

    public boolean isMovable() {

        switch (this.rank) {
            case FLAG:
            case BOMB:
            case LAKE:
                return false;
            default:
                return true;
        }

    }
}
