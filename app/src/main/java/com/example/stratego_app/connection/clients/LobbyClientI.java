package com.example.stratego_app.connection.clients;

import com.example.stratego_app.models.Player;

import java.util.List;

public interface LobbyClientI {
    void registerListener(LobbyClientListener listener);

    void unregisterListener(LobbyClientListener listener);

    void connect();

    void joinLobby(Player player);

    void leaveLobby(Player player);

    List<Player> getCurrentLobby();
}
