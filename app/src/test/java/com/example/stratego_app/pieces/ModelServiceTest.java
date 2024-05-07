package com.example.stratego_app.pieces;
import org.junit.jupiter.api.Test;
import static org.junit.Assert.*;


import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.pieces.*;

class ModelServiceTest {

    private ModelService modelService = new ModelService();


    @Test
     void getPieceTest() {
        assertNull(modelService.getPieceAtPosition(1,1));
    }

    @Test
     void getBoard() {
        assertNotNull(modelService.getBoard());
    }

}
