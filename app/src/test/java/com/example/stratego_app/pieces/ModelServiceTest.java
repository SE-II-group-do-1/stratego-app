package com.example.stratego_app.pieces;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.pieces.Piece;
import com.example.stratego_app.model.pieces.Rank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ModelServiceTest {

    private ModelService modelService = ModelService.getInstance();


    @BeforeEach
    void setUp() {
        modelService = ModelService.getInstance();
        modelService.clearBoardExceptLakes();
    }


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
