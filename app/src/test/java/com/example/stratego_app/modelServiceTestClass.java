package com.example.stratego_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.pieces.Board;
import com.example.stratego_app.model.pieces.Piece;
import com.example.stratego_app.model.pieces.Rank;

import org.junit.Before;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class modelServiceTestClass {

    private ModelService modelService = new ModelService();
    private Board board = new Board();

    @BeforeEach
    public void setUp() {
        modelService = ModelService.getInstance();
        board = modelService.getGameBoard();
    }

    @Test
    public void testPlacePieceAtGameSetUp_FailureOutsideSetupRows() {
        Piece piece = new Piece(Rank.MINER, null, 9);
        boolean result = modelService.placePieceAtGameSetUp(5, 5, piece); // Row 5 is not allowed

        assertFalse(result);
        assertNull( board.getField(5, 5));
    }

    @Test
    public void testPlacePieceAtGameSetUp_SuccessfulPlacement() {
        Piece piece = new Piece(Rank.MINER, null, 9);
        boolean result = modelService.placePieceAtGameSetUp(5, 6, piece);

        assertTrue(result);
        assertNotNull(modelService.getPieceAtPosition(5, 6));
        assertNotNull(board.getField(5,6));
        assertEquals(piece, modelService.getPieceAtPosition(5,6));
    }

    @Test
    void testClearBoardExceptLakes() {
        // Arrange
        modelService.getGameBoard().setField(0, 1, new Piece(Rank.FLAG));
        modelService.getGameBoard().setField(1, 0, new Piece(Rank.MARSHAL));

        // Act
        modelService.clearBoardExceptLakes();

        // Assert
        assertNull(modelService.getPieceAtPosition(0, 1));
        assertNull(modelService.getPieceAtPosition(1, 0));
    }


   @Test
    void testFillBoardRandomly_boardFull() {
        modelService.fillBoardRandomly();

        boolean allFilled = true;
        for (int y = 6; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (modelService.getPieceAtPosition(x, y) == null) {
                    allFilled = false;
                }
            }
        }
        assertTrue(allFilled);
    }
}
