package com.example.stratego_app;

//import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

import com.example.stratego_app.model.Color;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Rank;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(AndroidJUnit4.class)
class PieceTest {
    @Test
    public void testPieceInitialization() {
        Piece soldier = new Piece(Rank.SERGEANT, Color.BLUE);
        assertEquals("Rank should be SERGEANT", Rank.SERGEANT, soldier.getRank());
        assertEquals("Color should be BLUE", Color.BLUE, soldier.getColor());
        assertEquals("ID should be 1", 1, soldier.getId());
        assertFalse("Visibility should initially be false", soldier.isVisible());
        assertTrue("Sergeant should be movable", soldier.isMovable());
    }

    @Test
    public void testLakeInitialization() {
        Piece lake = new Piece(Rank.LAKE);
        assertEquals("Rank should be LAKE", Rank.LAKE, lake.getRank());
        assertTrue("Lakes should always be visible", lake.isVisible());
        assertFalse("Lakes should not be movable", lake.isMovable());
    }

    @Test
    public void testVisibilityToggle() {
        Piece flag = new Piece(Rank.FLAG, Color.RED);
        assertFalse("Visibility should initially be false", flag.isVisible());
        flag.setVisible(true);
        assertTrue("Visibility should be toggleable to true", flag.isVisible());
    }

    @Test
    public void testMovability() {
        // Checking all non-movable types
        Piece bomb = new Piece(Rank.BOMB, Color.RED);
        Piece flag = new Piece(Rank.FLAG, Color.BLUE);
        Piece lake = new Piece(Rank.LAKE);

        assertFalse("Bombs should not be movable", bomb.isMovable());
        assertFalse("Flags should not be movable", flag.isMovable());
        assertFalse("Lakes should not be movable", lake.isMovable());

        // Checking a movable type
        Piece captain = new Piece(Rank.CAPTAIN, Color.BLUE);
        assertTrue("Captains should be movable", captain.isMovable());
    }
}