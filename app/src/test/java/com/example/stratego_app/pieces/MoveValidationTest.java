package com.example.stratego_app.pieces;

import org.junit.jupiter.api.Test;
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
public class MoveValidationTest {
    private ModelService modelService = new ModelService();


    @Before
    public void setUp() {
        modelService = new ModelService();
    }
    @Test
    public void testMoveOutOfBoundary(){
        //TODO
    }
    @Test
    public void testMovePiece_NotValidMove() {
        // Arrange: Assuming you have a valid setup
        int startX = 1, startY = 1, endX = 1, endY = 1;
        modelService.getBoard().setField(startY, startX, new Piece(Rank.MINER));
        boolean expected = false;

        boolean result = modelService.movePiece(startX, startY, endX, endY);

        // Assert: Validate that the move was successful
        assertEquals("Valid move should be successful", expected, result);
    }
}
