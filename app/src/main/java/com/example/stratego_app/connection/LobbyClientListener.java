package com.example.stratego_app.connection;

import com.example.stratego_app.models.Player;

import java.util.List;

import ua.naiksoftware.stomp.dto.StompMessage;

public interface LobbyClientListener {

    void onLobbyResponse(StompMessage m);
}
