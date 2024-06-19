package com.example.stratego_app;

import android.content.Context;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.example.stratego_app.model.*;

public class SaveSetupTest {

    private Context context;

    private ModelService modelService;
    private Board gameBoard;
    private String username;

    @BeforeEach
    public void setUp() {
        context = Mockito.mock(Context.class);
        modelService = ModelService.getInstance();
        gameBoard = Mockito.mock(Board.class);
        modelService.getGameBoard();
        username = "claudia";
    }
    @Test
    public void testReadGameSetup_FileNotFound() {
        Piece[][] board = SaveSetup.readGameSetup(context, username);
        Assertions.assertNull(board);
    }

    @Test
    public void testSaveGameSetup_NullBoard() {
        ModelService.getInstance().getGameBoard();
        boolean result = SaveSetup.saveGameSetup(context, username);
        Assertions.assertFalse(result);
    }



}
