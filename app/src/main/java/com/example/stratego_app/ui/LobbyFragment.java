package com.example.stratego_app.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stratego_app.R;
import com.example.stratego_app.connection.clients.LobbyClient;
import com.example.stratego_app.models.Player;

import java.util.List;
import java.util.Set;


public class LobbyFragment extends Fragment {

    private LinearLayout playersContainer;
    public LobbyFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_lobby, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    private void displayUsernames(List<Player> players) {
        // Ensure playersContainer is not null
        if (playersContainer == null) {
            return;
        }

        // Clear existing views to prevent duplication
        playersContainer.removeAllViews();

        // Iterate over the list of players and add each to the view
        for (Player player : players) {
            addPlayerToView(player.getUsername());
        }
    }

    /*private void displayUsernames() {
        if (playersContainer == null) {
            return;
        }
        Set<String> usernames = MockSessionService.getUsernames();

        // Clear existing views to prevent duplication if this method is called again
        playersContainer.removeAllViews();

        for (String username : usernames) {
            addPlayerToView(username);
        }
    }*/
    private void addPlayerToView(String playerName) {
        TextView playerView = new TextView(getContext());
        playerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        playerView.setText(String.format("Player: %s     ... is ready to play.", playerName));

        playersContainer.addView(playerView);
    }


}