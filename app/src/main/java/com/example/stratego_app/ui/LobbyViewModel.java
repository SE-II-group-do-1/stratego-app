package com.example.stratego_app.ui;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

//class to check if 2 players are in the lobby for enabling startGame Button
public class LobbyViewModel extends ViewModel {
    private final MutableLiveData<Integer> numberOfPlayers = new MutableLiveData<>();

    public LobbyViewModel() {
        numberOfPlayers.setValue(0);  // Initialize with 0 players
    }

    public LiveData<Integer> getNumberOfPlayers() {
        return numberOfPlayers;
    }

    public void setNumberOfPlayers(int players) {
        numberOfPlayers.setValue(players);
    }
}
