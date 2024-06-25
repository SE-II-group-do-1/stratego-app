package com.example.stratego_app;

import android.content.Context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class StrategoTest {

    @Mock
    private Context mockContext;

    @Mock
    private Stratego mockStratego;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        mockStratego = mock(Stratego.class);
        when(mockStratego.getApplicationContext()).thenReturn(mockContext);

        Stratego.setInstance(mockStratego);
    }

    @Test
    public void testGetAppContext() {
        Context appContext = mockStratego.getApplicationContext();

        assertEquals(mockContext, appContext);
    }
    @Test
    public void testGetInstanceBeforeInitialization() {
        Stratego.setInstance(null);

        assertThrows(IllegalStateException.class, () -> {
            Stratego.getInstance();
        });
    }
}
