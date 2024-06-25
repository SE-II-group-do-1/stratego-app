package com.example.stratego_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.stratego_app.connection.LobbyMessage;
import com.example.stratego_app.model.Player;

class LobbyMessageTest {

    private LobbyMessage lobbyMessage;
    @BeforeEach
    void setup() {
        lobbyMessage = new LobbyMessage();
    }

    @Test
    void setLobbyIdOfLobbyMessage() {
        lobbyMessage.setLobbyID(0);
        assertEquals(0,lobbyMessage.getLobbyID());
    }

    @Test
    void setPlayerBlueOfLobbyMessage() {
        Player blue = new Player("Blue",0);
        lobbyMessage.setBlue(blue);
        assertEquals(lobbyMessage.getBlue(),blue);
    }

    @Test
    void setPlayerRedOfLobbyMessage() {
        Player red = new Player("Red",1);
        lobbyMessage.setRed(red);
        assertEquals(lobbyMessage.getRed(),red);
    }

}
