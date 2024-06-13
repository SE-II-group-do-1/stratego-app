package com.example.stratego_app.model;

public class Piece {
    private static int nextID = 0;
    private Rank rank;
    private boolean visible;
    private boolean movable;

    private Color color;

    public Piece(Rank rank, Color color){
        this.color = color;
        this.rank = rank;
        this.visible = false;
        this.movable = rank != Rank.LAKE && rank != Rank.FLAG && rank != Rank.BOMB;
        nextID++;
    }

    /**
     * Construcor specific for Lakes. ID is always -1 (do not affect next ID), have no color.
     * @param rank should be Lake, otherwise use other constructor
     */
    public Piece(Rank rank){
        if(rank != Rank.LAKE) new Piece(rank, ModelService.getInstance().getPlayerColor());
        this.rank = rank;
        this.visible = true;
        this.movable = false;
        this.color = null;
    }

    public Rank getRank() {
        return rank;
    }

    public boolean isVisible() {
        return visible;
    }

    public boolean isMovable() {
        return movable;
    }

    public Color getColor() {
        return color;
    }

    public void setColor(Color c){
        color = c;
    }

    public void setVisible(boolean b){
        this.visible = b;
    }
}
