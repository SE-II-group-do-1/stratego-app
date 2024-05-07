package com.example.stratego_app.ui;


import android.os.Bundle;

import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stratego_app.R;
import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.connection.LobbyClientListener;
import com.example.stratego_app.model.Player;


import java.util.List;


public class LobbyFragment extends Fragment implements LobbyClientListener {

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
        playersContainer = view.findViewById(R.id.playersContainer);

        LobbyClient lobbyClient = LobbyClient.getInstance();
        lobbyClient.registerListener(this);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        LobbyClient lobbyClient = LobbyClient.getInstance();
        lobbyClient.unregisterListener(this);

    }

    @Override
    public void onLobbyUpdated(List<Player> players) {
        if (getActivity() == null) return;

        getActivity().runOnUiThread(() -> {
            playersContainer.removeAllViews();
            for (Player player : players) {
                addPlayerToView(player.getUsername());
            }
        });
    }
    private void addPlayerToView(String playerName) {
        TextView playerView = new TextView(getContext());
        playerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        playerView.setText(String.format("Player: %s     ... is ready to play.", playerName));

        playersContainer.addView(playerView);
    }


}