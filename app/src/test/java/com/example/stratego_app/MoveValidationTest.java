package com.example.stratego_app;

import org.junit.AfterClass;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import static org.junit.Assert.*;

import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
 class MoveValidationTest {
    private ModelService modelService = new ModelService();
    @Test
     void testMoveOutOfBoundary(){
       boolean result = modelService.movePiece(0, 0, 10, 0); // End x is out of bounds
       assertFalse(result);
    }
    @Test
     void isMovable(){
        Piece bomb = new Piece(Rank.BOMB, Color.RED);
        modelService.getGameBoard().setField(0,0,bomb);
        assertEquals(false, modelService.movePiece(0,0,0,1));
    }
    @Test
     void checkForAPiece(){
        assertEquals(false, modelService.movePiece(0,0,0,1));
    }
    @Test
     void moveIntoLake(){
        Piece marshal = new Piece(Rank.MARSHAL, Color.RED);
        modelService.getGameBoard().setField(4,1,marshal);
        assertEquals(false, modelService.movePiece(4,1,4,2));
    }

    @Test
     void isMoveDiagonal(){
        Piece marshal = new Piece(Rank.MARSHAL, Color.RED);
        modelService.getGameBoard().setField(1,1,marshal);
        assertEquals(false, modelService.movePiece(1,1,2,2));
    }

    @Test
     void checkStepSize(){
        Piece marshal = new Piece(Rank.MARSHAL, Color.RED);
        modelService.getGameBoard().setField(0,0,marshal);
        assertEquals(false, modelService.movePiece(0,0,0,2));
    }

    @Test
     void isDestinationEmpty(){
        Piece marshal = new Piece(Rank.MARSHAL, Color.RED);
        modelService.getGameBoard().setField(0,0,marshal);
        assertEquals(true, modelService.movePiece(0,0,0,1));
    }

    @Test
     void DestinationNotEmpty(){
        Piece marshal = new Piece(Rank.MARSHAL, Color.RED);
        Piece jeff = new Piece(Rank.BOMB, Color.RED);
        modelService.getGameBoard().setField(0,0,marshal);
        modelService.getGameBoard().setField(1,0,jeff);
        assertEquals(false, modelService.movePiece(0,0,1,0));
    }
    @Test
     void testFight(){
        Piece marshal = new Piece(Rank.MARSHAL, Color.RED);
        Piece jeff = new Piece(Rank.MARSHAL, Color.BLUE);
        modelService.getGameBoard().setField(0,0,marshal);
        modelService.getGameBoard().setField(0,1,jeff);
        assertEquals(true, modelService.movePiece(0,0,0,1));
    }

    @Test
     void checkStepSizeScoutSomeoneInTheWayVertical(){
        Piece scout = new Piece(Rank.SCOUT, Color.RED);
        Piece jeff = new Piece(Rank.MARSHAL, Color.RED);
        modelService.getGameBoard().setField(0,0,scout);
        modelService.getGameBoard().setField(0,2,jeff);
        assertEquals(false, modelService.movePiece(0,0,0,5));
    }

    @Test
     void checkStepSizeScoutSomeoneInTheWayHorizontal(){
        Piece scout = new Piece(Rank.SCOUT, Color.RED);
        Piece jeff = new Piece(Rank.MARSHAL, Color.RED);
        modelService.getGameBoard().setField(0,0,scout);
        modelService.getGameBoard().setField(2,0,jeff);
        assertEquals(false, modelService.movePiece(0,0,5,0));
    }

    @Test
     void checkStepSizeScout(){
        Piece scout = new Piece(Rank.SCOUT, Color.RED);
        modelService.getGameBoard().setField(0,0,scout);
        assertEquals(true, modelService.movePiece(0,0,5,0));
    }


}
