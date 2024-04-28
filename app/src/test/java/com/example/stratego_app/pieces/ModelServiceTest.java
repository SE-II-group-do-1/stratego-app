package com.example.stratego_app.pieces;

//import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.junit.Before;
import static org.junit.Assert.*;

import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.pieces.*;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
//@RunWith(AndroidJUnit4.class)
class ModelServiceTest {

    private ModelService modelService = new ModelService();

    @Test
    public void getPieceTest() {
        assertNull(modelService.getPieceAtPosition(1,1));
    }

    @Test
    public void getBoard() {
        assertNotNull(modelService.getBoard());
    }


}
