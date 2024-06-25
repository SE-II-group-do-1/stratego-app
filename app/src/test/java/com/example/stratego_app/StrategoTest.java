package com.example.stratego_app;

import android.content.Context;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

public class StrategoTest {

    @Mock
    private Context mockContext;
    @Mock
    private Stratego mockStratego;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);

        // Initialize Stratego with the mocked context
        Stratego.setInstance(mockStratego);
    }

    @Test
    public void testGetAppContext() {
        // Get the application context from Stratego
        Context appContext = Stratego.getInstance().getAppContext();

        // Verify the application context is correctly returned
        assertEquals(mockContext.getApplicationContext(), mockStratego.getAppContext());
    }

    @Test
    public void testGetInstanceBeforeInitialization() {
        // Reset instance to null
        Stratego.setInstance(null);

        // Attempt to get the instance before initialization and assert that it throws an IllegalStateException
        assertThrows(IllegalStateException.class, Stratego::getInstance);
    }
}
