package com.example.stratego_app;

import static org.hamcrest.CoreMatchers.any;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.util.JsonWriter;

import com.example.stratego_app.model.GameState;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.ObserverModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Rank;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class ModelServiceTest {

    private ModelService modelService = new ModelService();
    private Board board = new Board();
    private ObserverModelService mockObserver;


    @BeforeEach
    public void setUp() throws Exception {
        modelService = ModelService.getInstance();
        board = modelService.getGameBoard();

        mockObserver = mock(ObserverModelService.class);
        modelService.addObserver(mockObserver);
        modelService.initializeGame(); // Assuming this method or similar can set the game setup mode.
    }

    @Test
    void getPieceTest() {
        assertNull(modelService.getPieceAtPosition(1,1));
    }

    @Test
    void getBoard() {
        assertNotNull(modelService.getGameBoard());
    }


    @Test
    void testStartGameEffectivelyStartsTheGame() {
        // Act
        modelService.startGame();

        // Assert
        verify(mockObserver, times(1)).onBoardUpdated();
        assertEquals(GameState.INGAME, modelService.getGameState());
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
        assertNotNull(board.getField(5,6));

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

    @Test
    public void testRemoveObserver() {
        ObserverModelService observer = mock(ObserverModelService.class);
        ModelService modelService = ModelService.getInstance();
        modelService.addObserver(observer);
        modelService.removeObserver(observer);
        modelService.setGameState(GameState.INGAME); // Change to trigger notification
        verify(observer, times(0)).onBoardUpdated(); // Observer should not be called
    }

    @Test
    public void testSetAndGetGameState() {
        ModelService modelService = ModelService.getInstance();
        modelService.setGameState(GameState.INGAME);
        assertEquals(GameState.INGAME, modelService.getGameState());
        modelService.setGameState(GameState.WAITING);
        assertEquals(GameState.WAITING, modelService.getGameState());
    }


    @Test
    public void testCreateOrUpdatePlayer() {
        ModelService modelService = ModelService.getInstance();
        modelService.createOrUpdatePlayer("player1", 1);
        assertNotNull(modelService.getCurrentPlayer());
        assertEquals("player1", modelService.getCurrentPlayer().getUsername());
        assertEquals(1, modelService.getCurrentPlayer().getId());

        // Update the same player
        modelService.createOrUpdatePlayer("playerUpdated", 2);
        assertEquals(2, modelService.getCurrentPlayer().getId());
    }


    @Test
    public void testInitializeGame() {
        ModelService modelService = ModelService.getInstance();
        modelService.initializeGame();
        assertNotNull(modelService.getGameBoard());
        // Further assertions can be made based on the initial state of the Board
    }


}
