package com.example.stratego_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.stratego_app.connection.UpdateMessage;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Color;

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

    @Test
    void setWinnerForUpdateMessage() {
        Color winner = Color.RED;
        updateMessage.setWinner(winner);
        assertEquals(Color.RED,updateMessage.getWinner());
    }

    @Test
    void testSetCheat() {
        updateMessage.setCheat(true);
        assertTrue(updateMessage.getCheat());
    }

    @Test
    void testSetCheck() {
        updateMessage.setCheck(true);
        assertTrue(updateMessage.getCheck());
    }

    @Test
    void testResetCheat() {
        updateMessage.setCheat(true);
        updateMessage.setCheat(false);
        assertFalse(updateMessage.getCheat());
    }

    @Test
    void testResetCheck() {
        updateMessage.setCheck(true);
        updateMessage.setCheck(false);
        assertFalse(updateMessage.getCheck());
    }

    @Test
    void testSetAndGetClose() {
        updateMessage.setClose(true);
        assertTrue(updateMessage.getClose());

        updateMessage.setClose(false);
        assertFalse(updateMessage.getClose());
    }
}
