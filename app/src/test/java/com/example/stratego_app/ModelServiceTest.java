package com.example.stratego_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import com.example.stratego_app.model.GameState;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.ObserverModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Rank;
import org.junit.Assert;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ModelServiceTest {

    private ModelService modelService = new ModelService();
    private Board board = new Board();
    private ObserverModelService mockObserver;


    @BeforeEach
    public void setUp() throws Exception {
        board = modelService.getGameBoard();

        mockObserver = mock(ObserverModelService.class);
        ModelService.subscribe(mockObserver);
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
        //modelService.startGame();

        // Assert
        verify(mockObserver, times(1)).update();
        assertEquals(GameState.INGAME, modelService.getGameState());
    }


    @Test
    public void testPlacePieceAtGameSetUp_FailureOutsideSetupRows() {
        Piece piece = new Piece(Rank.MINER, null);
        boolean result = modelService.placePieceAtGameSetUp(5, 5, piece); // Row 5 is not allowed

        assertFalse(result);
        assertNull( board.getField(5, 5));
    }

    @Test
    public void testPlacePieceAtGameSetUp_SuccessfulPlacement() {
        Piece piece = new Piece(Rank.MINER, null);
        boolean result = modelService.placePieceAtGameSetUp(5, 6, piece);
        Assert.assertNotNull(board.getField(5,6));
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
        ModelService.subscribe(observer);
        ModelService.unsubscribe(observer);
        modelService.setGameState(GameState.INGAME); // Change to trigger notification
        verify(observer, times(0)).update(); // Observer should not be called
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
        modelService.Player("player1", 1);
        assertNotNull(modelService.getPlayer());
        assertEquals("player1", modelService.getCurrentPlayer().getUsername());
        assertEquals(1, modelService.getCurrentPlayer().getId());

        // Update the same player
        modelService.Player("playerUpdated", 2);
        assertEquals(2, modelService.getCurrentPlayer().getId());
    }


    @Test
    public void testInitializeGame() {
        ModelService modelService = ModelService.getInstance();
        assertNotNull(modelService.getGameBoard());
        // Further assertions can be made based on the initial state of the Board
    }


}
