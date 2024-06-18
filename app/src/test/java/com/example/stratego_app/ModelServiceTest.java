package com.example.stratego_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.model.Color;
import com.example.stratego_app.model.GameState;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.ObserverModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Player;
import com.example.stratego_app.model.Rank;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


public class ModelServiceTest {

    private ModelService modelService;
    private Board board = new Board();
    private ObserverModelService mockObserver;


    @BeforeEach
    public void setUp() {
        modelService = ModelService.getInstance();
        board = modelService.getGameBoard();
        mockObserver = mock(ObserverModelService.class);
        ModelService.subscribe(mockObserver);
    }

    @AfterEach
    public void validate() {
        validateMockitoUsage();
        modelService.newInstance();
    }

    @Test
    void getBoard() {
        assertNotNull(modelService.getGameBoard());
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
        modelService.placePieceAtGameSetUp(5, 6, piece);
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
        assertNull(modelService.getGameBoard().getField(0, 1));
        assertNull(modelService.getGameBoard().getField(1, 0));
    }


   @Test
    void testFillBoardRandomly_boardFull() {
        modelService.fillBoardRandomly();

        boolean allFilled = true;
        for (int y = 6; y < 10; y++) {
            for (int x = 0; x < 10; x++) {
                if (modelService.getGameBoard().getField(y, x) == null) {
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
        modelService.Player(new Player("player1", 1));
        assertNotNull(modelService.getCurrentPlayer());
        assertEquals("player1", modelService.getCurrentPlayer().getUsername());
        assertEquals(1, modelService.getCurrentPlayer().getId());

        // Update the same player
        modelService.Player(new Player("playerUpdated", 2));
        assertEquals(2, modelService.getCurrentPlayer().getId());
    }


    @Test
    public void testInitializeGame() {
        ModelService modelService = ModelService.getInstance();
        assertNotNull(modelService.getGameBoard());
        // Further assertions can be made based on the initial state of the Board
    }

    @Test
    public void testNotifyUI() {
        ModelService.notifyUI();
        verify(mockObserver, times(1)).update();
    }

    @Test
    public void testOpponent() {
        Player opponent = new Player("opponent", 2);
        modelService.Opponent(opponent);
        assertEquals(opponent, modelService.getCurrentOpponent());
    }

    @Test
    public void testMovePiece_InvalidMove() {
        // Arrange
        Piece piece = new Piece(Rank.MARSHAL, Color.RED);
        modelService.getGameBoard().setField(0, 0, piece);

        // Invalid move (out of bounds)
        boolean result = modelService.movePiece(0, 0, 8, 8);

        // Assert
        assertFalse(result);
        //assertEquals(piece, board.getField(0, 0));
        assertNull(modelService.getGameBoard().getField(8, 8));
        verify(mockObserver, times(0)).update(); // No notification should happen
    }

    @Test
    void testAreTwoPlayersConnected_BothNotNull() {
        ModelService modelService = ModelService.getInstance();
        modelService.Player(new Player("player1", 1));
        modelService.Opponent(new Player("opponent", 2));
        assertTrue(modelService.getCurrentPlayer() != null && modelService.getCurrentOpponent() != null);
    }



    @Test
    void testAreTwoPlayersConnected_CurrentPlayerNull() {
        ModelService modelService = ModelService.getInstance();
        modelService.Player(null);
        modelService.Opponent(new Player("opponent", 2));
        assertFalse(modelService.getCurrentPlayer() != null && modelService.getCurrentOpponent() != null);
    }

    @Test
    void testAreTwoPlayersConnected_CurrentOpponentNull() {
        ModelService modelService = ModelService.getInstance();
        modelService.Player(new Player("player1", 1));
        modelService.Opponent(null);
        assertFalse(modelService.getCurrentPlayer() != null && modelService.getCurrentOpponent() != null);
    }

    @Test
    void testAreTwoPlayersConnected_BothNull() {
        ModelService modelService = ModelService.getInstance();
        modelService.Player(null);
        modelService.Opponent(null);
        assertFalse(modelService.getCurrentPlayer() != null && modelService.getCurrentOpponent() != null);
    }

    @Test
    void testIsSetupComplete_AtLeastOneFieldEmpty() {
        Board newBoard = mock(Board.class);

        ModelService modelService = ModelService.getInstance();
        fillBoardForSetupWithOneEmpty(board);

        assertFalse(modelService.isSetupComplete());
    }

    @Test
    void testCheckForRotationRed(){
        modelService.getGameBoard().setField(0,0, new Piece(Rank.GENERAL));
        modelService.setPlayerColor(Color.RED);
        Board n = ModelService.checkForRotation(modelService.getGameBoard());
        assertEquals(Rank.GENERAL, n.getField(9,9).getRank());
    }

    @Test
    void testCheckForRotationBlue(){
        modelService.getGameBoard().setField(0,0, new Piece(Rank.GENERAL));
        modelService.setPlayerColor(Color.BLUE);
        Board n = ModelService.checkForRotation(modelService.getGameBoard());
        assertEquals(Rank.GENERAL, n.getField(0,0).getRank());

    }
    @Test
    void testSetPlayerColor(){
        modelService.getGameBoard().setField(9,9, new Piece(Rank.GENERAL));
        modelService.setPlayerColor(Color.RED);
        assertEquals(Color.RED, modelService.getGameBoard().getField(9,9).getColor());
    }

    @Test
    void testLeaveGame(){
        ModelService mockModel = mock(ModelService.class);
        mockModel.setGameState(GameState.INGAME);
        mockModel.leaveGame();
        verify(mockModel, times(1)).leaveGame();
    }

    @Test
    public void testLeaveGame_NotInGame() {
        ModelService mockModel = mock(ModelService.class);
        verify(mockModel, never()).leaveGame();
        verify(mockModel, never()).newInstance();
    }

    @Test
    void testUpdateBoardNull(){
        modelService.updateBoard(null);
        assertNotNull(modelService.getGameBoard());
    }

    @Test
    void testUpdateBoardRed(){
        Board b = new Board();
        b.setField(0,0, new Piece(Rank.GENERAL));
        modelService.setPlayerColor(Color.RED);
        modelService.updateBoard(b);
        assertTrue(modelService.getGameBoard().getField(9,9).getRank() == Rank.GENERAL);
    }

    @Test
    void testUpdateBoardBlue(){
        Board b = new Board();
        b.setField(0,0, new Piece(Rank.GENERAL));
        modelService.setPlayerColor(Color.BLUE);
        modelService.updateBoard(b);
        assertTrue(modelService.getGameBoard().getField(0,0).getRank() == Rank.GENERAL);
    }

    // Helper methods to set up the board for tests

    private void fillBoardForSetupWithOneEmpty(Board board) {
        for (int y = 6; y < 10; y++) {
            for (int x = 0; x < 9; x++) {
                board.setField(y, x, new Piece(Rank.SPY));
            }
        }
        board.setField(9, 9, null);
    }

    @Test
    public void testCheckWinWhenPlayerWins() {
        modelService.setPlayerColor(Color.BLUE);

        assertTrue( modelService.checkWin(Color.BLUE));
    }


    @Test
    public void testCheckWinWhenPlayerLoses() {

        modelService.setPlayerColor(Color.BLUE);

        assertTrue(modelService.checkWin(Color.RED));
    }

    @Test
    public void testCheckWinWhenWinnerIsNull() {

        modelService.setPlayerColor(Color.BLUE);
        assertFalse( modelService.checkWin(null));
    }

}
