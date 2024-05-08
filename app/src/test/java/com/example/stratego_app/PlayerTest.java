package com.example.stratego_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import com.example.stratego_app.model.Player;

class PlayerTest {
    private Player player;

    @BeforeEach
    void setUp() {
        player = new Player("testUsername", 1);
    }

    @Test
    void testPlayerInitializationWithUsername() {
        assertEquals("testUsername", player.getUsername());
    }

    @Test
    void testSetUsername() {
        player.setUsername("newUsername");
        assertEquals("newUsername", player.getUsername());
    }

    @Test
    void testGetIdBeforeSet() {
        assertEquals(0, player.getId());
    }

    @Test
    void testSetId() {
        player.setId(1234);
        assertEquals(1234, player.getId());
    }

    @Test
    void testPlayerToString() {
        player.setId(1);
        String expectedString = "Player{username='testUsername', id=1}";
        assertEquals(expectedString, player.toString());
    }
}

