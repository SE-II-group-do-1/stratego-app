package com.example.stratego_app.model;

import android.util.Log;

public class Piece {
    private static int nextID = 0;
    private Rank rank;
    private boolean isVisible;
    private boolean isMovable;

    private Color color;
    private int id;

    public Piece(Rank rank, Color color){
        this.id = nextID;
        this.color = color;
        this.rank = rank;
        this.isVisible = false;
        this.isMovable = rank != Rank.LAKE && rank != Rank.FLAG && rank != Rank.BOMB;
        nextID++;
    }

    /**
     * Construcor specific for Lakes. ID is always -1 (do not affect next ID), have no color.
     * @param rank should be Lake, otherwise use other constructor
     */
    public Piece(Rank rank){
        if(rank != Rank.LAKE) Log.e("Piece", "Wrong Constructor!!!!!");
        this.rank = rank;
        this.isVisible = true;
        this.isMovable = false;
        this.id = -1;
        this.color = null;
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

    public Color getColor() {
        return color;
    }

    public int getId() {
        return id;
    }
}
