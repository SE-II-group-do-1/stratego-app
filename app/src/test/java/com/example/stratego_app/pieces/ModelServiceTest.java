package com.example.stratego_app.pieces;

//import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import static org.junit.Assert.*;

import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.pieces.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(AndroidJUnit4.class)
public class ModelServiceTest {

    private ModelService modelService = new ModelService();
    @Test
    public void testMovePiece_ValidMove() {
        // Arrange: Assuming you have a valid setup
        int startX = 2, startY = 1, endX = 2, endY = 1;
        modelService.getBoard().setField(startY, startX, new Piece(Rank.MINER));
        boolean expected = true;

        boolean result = modelService.movePiece(startX, startY, endX, endY);

        // Assert: Validate that the move was successful
        assertEquals("Valid move should be successful", expected, result);
    }
    @Test
    public void testMovePiece_NotValidMove() {
        // Arrange: Assuming you have a valid setup
        int startX = 1, startY = 1, endX = 2, endY = 2;
        modelService.getBoard().setField(startY, startX, new Piece(Rank.MINER));
        boolean expected = false;

        boolean result = modelService.movePiece(startX, startY, endX, endY);

        // Assert: Validate that the move was successful
        assertEquals("Move should not be successful", expected, result);
    }
    @Test
    public void getPieceTest() {
        assertNull(modelService.getPieceAtPosition(1,1));
    }

    @Test
    public void getBoard() {
        assertNotNull(modelService.getBoard());
    }


}
