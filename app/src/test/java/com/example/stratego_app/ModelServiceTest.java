package com.example.stratego_app;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.validateMockitoUsage;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;

import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.model.Color;
import com.example.stratego_app.model.GameState;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.ObserverModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Player;
import com.example.stratego_app.model.Position;
import com.example.stratego_app.model.Rank;
import org.junit.Assert;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.lang.reflect.Field;


public class ModelServiceTest {

    private ModelService modelService;
    private Board board = new Board();
    private ObserverModelService mockObserver;
    private SensorManager mockSensorManager;
    private Sensor mockSensor;
    @Mock
    private Context mockContext;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        Stratego mockStratego = mock(Stratego.class);
        when(mockStratego.getAppContext()).thenReturn(mockContext);

        Stratego.setInstance(mockStratego);

        modelService = ModelService.getInstance();
        board = modelService.getGameBoard();
        mockObserver = mock(ObserverModelService.class);
        ModelService.subscribe(mockObserver);

        mockSensorManager = mock(SensorManager.class);
        mockSensor = mock(Sensor.class);
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
    public void testShakeDetection() throws NoSuchFieldException, IllegalAccessException {
        // Register sensor listener
        modelService.registerSensorListener();

        // Create a mock sensor event
        SensorEvent mockEvent = createSensorEvent(new float[]{12.0f, 12.0f, 12.0f});

        // Simulate sensor event
        modelService.onSensorChanged(mockEvent);

        // Verify that cheatingActivated is toggled
        assertEquals(true, modelService.isCheatingActivated());

        // Simulate sensor event again to toggle off
        modelService.onSensorChanged(mockEvent);
        assertFalse(modelService.isCheatingActivated());
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
    public void testHorizontalClearPath() {
        int startX = 1;
        int endX = 1;
        int startY = 1;
        int endY = 5;

        boolean isPathClear = modelService.iterateOverAllIntermediateSpaces(startX, endX, startY, endY);

        assertTrue(isPathClear);
    }
    @Test
    public void testVerticalClearPath() {
        int startX = 3;
        int endX = 1;
        int startY = 3;
        int endY = 4;

        boolean isPathClear = modelService.iterateOverAllIntermediateSpaces(startX, endX, startY, endY);

        assertTrue(isPathClear);
    }


    @Test
    public void testVerticalObstructedPath() {
        Board gameBoard = modelService.getGameBoard();
        gameBoard.setField(3, 3, new Piece(Rank.SPY, Color.RED));

        assertFalse(modelService.iterateOverAllIntermediateSpaces(3, 3, 1, 4));
    }

    @Test
    public void testNoMovement() {
        assertTrue(modelService.iterateOverAllIntermediateSpaces(1, 1, 1, 1));
    }

    @Test
    public void testOneStepLeft() {
        int startX = 3;
        int endX = 2;
        int startY = 3;
        int endY = 3;

        boolean isPathClear = modelService.iterateOverAllIntermediateSpaces(startX, endX, startY, endY);

        assertTrue(isPathClear);
    }

    @Test
    public void testOneStepRigth() {
        int startX = 3;
        int endX = 3;
        int startY = 3;
        int endY = 3;

        boolean isPathClear = modelService.iterateOverAllIntermediateSpaces(startX, endX, startY, endY);

        assertTrue(isPathClear);
    }

    @Test
    public void testCheckWin() {
        modelService.setPlayerColor(Color.BLUE);

        modelService.checkWin(Color.BLUE);
        assertEquals(GameState.WIN, modelService.getGameState());

        modelService.checkWin(Color.RED);
        assertEquals(GameState.LOSE, modelService.getGameState());

        modelService.checkWin(null);
        assertEquals(GameState.LOSE, modelService.getGameState());
    }

    @Test
    public void testCheckForRotationPos(){
        modelService.setPlayerColor(Color.BLUE);
        Position p = new Position(1,2);
        assertEquals(p, ModelService.checkForRotationPos(p));
    }

    @Test
    public void testCheckForRotationPosRed(){
        modelService.setPlayerColor(Color.RED);
        Position p = new Position(9,4);
        Position rotated = new Position(0, 5);
        assertEquals(rotated.getX(), ModelService.checkForRotationPos(p).getX());
        assertEquals(rotated.getY(), ModelService.checkForRotationPos(p).getY());
    }

    @Test
    public void testGetAndSetOldPos(){
        Position p = new Position(0,0);
        modelService.setOldPos(p);
        assertEquals(p, modelService.getOldPos());
    }

    @Test
    public void testGetAnsSetNewPos(){
        Position p = new Position(3,4);
        modelService.setNewPos(p);
        assertEquals(p, modelService.getNewPos());
    }

    private SensorEvent createSensorEvent(float[] values) throws NoSuchFieldException, IllegalAccessException {
        SensorEvent event = mock(SensorEvent.class);
        Field valuesField = SensorEvent.class.getDeclaredField("values");
        valuesField.setAccessible(true);
        valuesField.set(event, values);
        return event;
    }

}





