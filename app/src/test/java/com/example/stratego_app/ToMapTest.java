package com.example.stratego_app;

import static org.junit.Assert.assertEquals;

import com.example.stratego_app.connection.ToMap;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Player;

import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

public class ToMapTest {

    @Test
    void testUpdateToObject(){
        Board b = new Board();
        Player player = new Player("TEST", 1);

        Map<String, Object> testMap = new HashMap<>();
        testMap.put("board", b);
        testMap.put("initiator", player.getId());
        testMap.put("lobby", 1);

        assertEquals(testMap, ToMap.updateToObject(b,player.getId(), 1));

    }

}
