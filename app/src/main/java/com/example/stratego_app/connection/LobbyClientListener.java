package com.example.stratego_app.connection;

import com.example.stratego_app.model.Player;

import java.util.List;

public interface LobbyClientListener {

    void onLobbyUpdated(String message);
}
