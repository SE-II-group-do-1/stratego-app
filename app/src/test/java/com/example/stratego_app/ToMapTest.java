package com.example.stratego_app;

import static org.junit.Assert.assertEquals;

import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.connection.ToMap;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Color;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Rank;
import com.example.stratego_app.model.Player;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ToMapTest {

    @Test
    void testUpdateToObject(){
        int y = 1;
        int x = 2;
        Piece piece = new Piece(Rank.MAJOR, Color.BLUE);
        Player player = new Player("TEST", 1);

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("y", y);
        testMap.put("x", x);
        testMap.put("piece", piece);
        testMap.put("initiator", player);

        assertEquals(testMap, ToMap.updateToObject(y,x,piece,player));

    }

    @Test
    void testLeaveToObject(){
        Player player = new Player("TEST", 1);
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("id", LobbyClient.getInstance().getCurrentLobby());
        testMap.put("player", player);

        assertEquals(testMap, ToMap.leaveToObject(player));
    }

    @Test
    void testSetupToObject(){
        Player player = new Player("TEST", 1);
        Board board = new Board();
        Map<String, Object> testMap = new HashMap<>();
        testMap.put("board", board);
        testMap.put("player", player);

        assertEquals(testMap, ToMap.setupToObject(player, board));
    }
}
