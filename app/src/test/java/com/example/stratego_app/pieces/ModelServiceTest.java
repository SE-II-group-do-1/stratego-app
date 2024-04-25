package com.example.stratego_app.pieces;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;


import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.pieces.*;

class ModelServiceTest {

    private ModelService modelService = new ModelService();


    @Test
     void testMovePiece_ValidMove() {
        // Arrange: Assuming you have a valid setup
        int startX = 2, startY = 1, endX = 2, endY = 1;
        modelService.getBoard().setField(startY, startX, new Piece(Rank.MINER));
        boolean expected = true;

        boolean result = modelService.movePiece(startX, startY, endX, endY);

        // Assert: Validate that the move was successful
        assertEquals("Valid move should be successful", expected, result);
    }
    @Test
     void testMovePiece_NotValidMove() {
        // Arrange: Assuming you have a valid setup
        int startX = 1, startY = 1, endX = 2, endY = 2;
        modelService.getBoard().setField(startY, startX, new Piece(Rank.MINER));
        boolean expected = false;

        boolean result = modelService.movePiece(startX, startY, endX, endY);

        // Assert: Validate that the move was successful
        assertEquals("Move should not be successful", expected, result);
    }
    @Test
     void getPieceTest() {
        assertNull(modelService.getPieceAtPosition(1,1));
    }

    @Test
     void getBoard() {
        assertNotNull(modelService.getBoard());
    }

}
