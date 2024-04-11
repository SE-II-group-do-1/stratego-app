package com.example.stratego_app.pieces;

//import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.stratego_app.model.pieces.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(AndroidJUnit4.class)
public class BoardTest {
    private Board board = new Board();

    @Test
    public void testConstructor() {
        assertNotNull(board.getBoard());
    }
    @Test
    public void testLakes() {
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
    public void testSetandGetField(){
        board.setField(1,1, new Piece(Rank.FLAG));
        assertEquals(Rank.FLAG, board.getField(1, 1).getRank());
    }
    @Test
    public void testSetAndGetBoard(){
        Board secondBoard = new Board();
        secondBoard.setField(1,2, new Piece(Rank.MAJOR));
        board.setBoard(secondBoard);
        assertArrayEquals(secondBoard.getBoard(), board.getBoard());

    }


}
