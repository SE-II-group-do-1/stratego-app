package com.example.stratego_app;

//import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.Assert.*;

import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Color;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Rank;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(AndroidJUnit4.class)
class BoardTest {
    private Board board = new Board();
    @BeforeEach
    void setUp() {
        board = new Board();
    }

    @Test
    void testBoardInitialization() {
        assertNotNull("Piece at lake position should be initialized", board.getField(4, 2));
        assertEquals("Should be a lake", Rank.LAKE, board.getField(4, 2).getRank());
    }



    @Test
    void testConstructor() {
        assertNotNull(board.getBoard());
    }
    @Test
    void testLakes() {
        assertEquals(Rank.LAKE, board.getField(4,2).getRank());
        assertEquals(Rank.LAKE, board.getField(4,3).getRank());
        assertEquals(Rank.LAKE, board.getField(4,6).getRank());
        assertEquals(Rank.LAKE, board.getField(4,7).getRank());
        assertEquals(Rank.LAKE, board.getField(5,2).getRank());
        assertEquals(Rank.LAKE, board.getField(5,3).getRank());
        assertEquals(Rank.LAKE, board.getField(5,6).getRank());
        assertEquals(Rank.LAKE, board.getField(5,7).getRank());
    }

    @Test
    void testSetandGetField(){
        board.setField(1,1, new Piece(Rank.FLAG));
        assertEquals(Rank.FLAG, board.getField(1, 1).getRank());
    }
    @Test
    void testSetAndGetBoard(){
        board.setField(1,2, new Piece(Rank.MAJOR));
        this.board.setBoard(board);
        assertArrayEquals(board.getBoard(), this.board.getBoard());

    }

    @Test
    void testIsValidLocation_withValidCoordinates() {
        assertTrue(board.isValidLocation(1, 1));
    }

    @Test
    void testIsValidLocation_withBoundaryCoordinates() {
        assertTrue(board.isValidLocation(0, 0));
        assertTrue(board.isValidLocation(9, 9));
        assertTrue(board.isValidLocation(0, 9));
        assertTrue(board.isValidLocation(9, 0));
    }

    @Test
    void testIsValidLocation_withInvalidCoordinates() {
        assertFalse(board.isValidLocation(-1, 5));
        assertFalse(board.isValidLocation(10, 5));
        assertFalse(board.isValidLocation(5, -1));
        assertFalse(board.isValidLocation(5, 10));
    }

    @Test
    void testIsValidLocation_withLakePositions() {
        assertFalse(board.isValidLocation(4, 2));
        assertFalse(board.isValidLocation(4, 3));
        assertFalse(board.isValidLocation(5, 2));
        assertFalse(board.isValidLocation(5, 3));
        assertFalse(board.isValidLocation(4, 6));
        assertFalse(board.isValidLocation(4, 7));
        assertFalse(board.isValidLocation(5, 6));
        assertFalse(board.isValidLocation(5, 7));
    }

    @Test
    void testBoardRotation() {
        Piece flagPieceFromBlue = new Piece(Rank.FLAG, Color.BLUE);
        board.setField(0,0,flagPieceFromBlue);

        board.rotateBoard();
        assertEquals(flagPieceFromBlue,board.getField(9,9));
    }




}
