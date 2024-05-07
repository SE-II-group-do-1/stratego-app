package com.example.stratego_app.pieces;

//import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.stratego_app.model.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
 class RankTest {
    private Board board = new Board();

    @Test
     void testGetRank(){
        assertEquals(Rank.LAKE, board.getField(4,2).getRank());
    }



}
