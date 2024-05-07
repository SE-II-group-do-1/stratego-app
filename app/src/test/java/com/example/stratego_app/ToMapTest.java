package com.example.stratego_app;

import static org.junit.Assert.assertEquals;

import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.connection.ToMap;
import com.example.stratego_app.model.pieces.Board;
import com.example.stratego_app.model.pieces.Color;
import com.example.stratego_app.model.pieces.Piece;
import com.example.stratego_app.model.pieces.Rank;
import com.example.stratego_app.models.Player;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ToMapTest {

    @Test
    void testUpdateToObject(){
        int y = 1;
        int x = 2;
        Piece piece = new Piece(Rank.MAJOR, Color.BLUE, 1);
        Player player = new Player(1, "TEST");

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("y", y);
        testMap.put("x", x);
        testMap.put("piece", piece);
        testMap.put("initiator", player);

        assertEquals(testMap, ToMap.updateToObject(y,x,piece,player));

    }

    @Test
    void testLeaveToObject(){
        Player player = new Player();
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("id", LobbyClient.getInstance().getCurrentLobby());
        testMap.put("player", player);

        assertEquals(testMap, ToMap.leaveToObject(player));
    }

    @Test
    void testSetupToObject(){
        Player player = new Player();
        Board board = new Board();
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("board", board);
        testMap.put("player", player);

        assertEquals(testMap, ToMap.setupToObject(player, board));
    }
}
