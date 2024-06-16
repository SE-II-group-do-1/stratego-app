package com.example.stratego_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.stratego_app.connection.UpdateMessage;
import com.example.stratego_app.model.Board;

public class UpdateMessageTest {
    private UpdateMessage updateMessage;

    @BeforeEach
    void setup() {
        updateMessage = new UpdateMessage();
    }

    @Test
    void setBoardForUpdateMessage() {
        Board b = new Board();
        updateMessage.setBoard(b);
        assertEquals(updateMessage.getBoard(),b);
    }
    @Test
    void setLobbyIdForUpdateMessage() {
        int lobbyid = 0;
        updateMessage.setLobbyID(lobbyid);
        assertEquals(updateMessage.getLobbyID(), 0);
    }
    @Test
    void setInitiatorForUpdateMessage() {
        updateMessage.setInitiator(0);
        assertEquals(updateMessage.getInitiator(),0);
    }
}
