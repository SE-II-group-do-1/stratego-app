package com.example.stratego_app.connection;

import java.util.Objects;

public class Player {
    private String username;
    private int id;

    public Player() {
        // Default constructor
    }

    public Player(int id, String username) {
        this.id = id;
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
