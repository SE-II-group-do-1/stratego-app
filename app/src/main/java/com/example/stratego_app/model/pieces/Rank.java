package com.example.stratego_app.model.pieces;

public enum Rank {
    MARSHAL(10),
    GENERAL(9),
    COLONEL(8),
    MAJOR(7),
    CAPTAIN(6),
    LIEUTENANT(5),
    SERGEANT(4),
    MINER(3),
    SCOUT(2),
    SPY(1),
    FLAG(0),
    BOMB(-1),
    LAKE(-2);

    private final int value;

    // Constructor to initialize the value for each rank
    Rank(int value) {
        this.value = value;
    }

    // Method to retrieve the value of the rank
    public int getValue() {
        return value;
    }
}
