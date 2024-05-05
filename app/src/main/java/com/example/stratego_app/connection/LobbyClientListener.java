package com.example.stratego_app.connection;

import com.example.stratego_app.models.Player;

import java.util.List;

public interface LobbyClientListener {

    void onLobbyUpdated(List<Player> players);
}
