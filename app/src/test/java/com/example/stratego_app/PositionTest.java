package com.example.stratego_app;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.example.stratego_app.model.Position;

import org.junit.jupiter.api.Test;

public class PositionTest {
    @Test
    void testConstructor() {
        Position position = new Position(5, 10);
        assertEquals(5, position.getX());
        assertEquals(10, position.getY());
    }

    @Test
    void testGetX() {
        Position position = new Position(3, 7);
        assertEquals(3, position.getX());
    }

    @Test
    void testSetX() {
        Position position = new Position(0, 0);
        position.setX(8);
        assertEquals(8, position.getX());
    }

    @Test
    void testGetY() {
        Position position = new Position(4, 9);
        assertEquals(9, position.getY());
    }

    @Test
    void testSetY() {
        Position position = new Position(0, 0);
        position.setY(6);
        assertEquals(6, position.getY());
    }
}
