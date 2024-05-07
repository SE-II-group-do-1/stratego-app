package com.example.stratego_app.model;

public class Player {
    private String username;
    private int id;

    public Player() {
        // Default constructor
    }

    public Player(String username) { //id is set by server --> Update MainFragment to receive correct player id!
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "Player{" +
                "username='" + username + '\'' +
                ", id=" + id +
                '}';
    }
}
