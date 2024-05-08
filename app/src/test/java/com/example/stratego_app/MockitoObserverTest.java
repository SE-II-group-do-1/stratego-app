package com.example.stratego_app;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.Mockito.*;

import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.ObserverModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Rank;

@ExtendWith(MockitoExtension.class)
class MockitoObserverTest {
    @Mock
    private ObserverModelService observer;
    private ModelService modelService;


    @BeforeEach
    void setUp() {
        ModelService.subscribe(observer);
    }


    @Test
    void testNotifyObservers_OnPiecePlacement() {
        modelService.placePieceAtGameSetUp(7, 6, new Piece(Rank.MARSHAL));
        verify(observer, times(1)).update();
    }

    @Test
    void testNotifyObservers_OnClearBoard() {
        modelService.clearBoardExceptLakes();
        verify(observer, times(1)).update();
    }

}
