package com.example.stratego_app.ui;

import java.util.HashSet;
import java.util.Set;

public class MockSessionService {
    private static final Set<String> usernames = new HashSet<>();

    private MockSessionService() {
        throw new UnsupportedOperationException("This is a utility class and cannot be instantiated");
    }

    // Simulate fetching usernames from the server
    public static Set<String> getUsernames() {
        return new HashSet<>(usernames);
    }

    public static void addUsername(String username) {
        usernames.add(username);
    }

    public static void clearUsernames() {
        usernames.clear();
    }
}
