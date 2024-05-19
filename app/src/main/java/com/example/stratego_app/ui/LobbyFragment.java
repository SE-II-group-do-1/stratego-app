package com.example.stratego_app.ui;


import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.stratego_app.R;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.ObserverModelService;


public class LobbyFragment extends Fragment implements ObserverModelService {

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

        ModelService.subscribe(this);
    }

    @Override
    public void onDestroyView() {
        ModelService.unsubscribe(this);
        super.onDestroyView();

    }

    @Override
    public void update() {
        if (getActivity() == null) return;
        String ownName = (ModelService.getInstance().getCurrentPlayer() == null)? "null" : ModelService.getInstance().getCurrentPlayer().getUsername();
        String oppName = (ModelService.getInstance().getCurrentOpponent() == null)? "null" : ModelService.getInstance().getCurrentOpponent().getUsername();

        getActivity().runOnUiThread(() -> {
            playersContainer.removeAllViews();
            addPlayerToView(ownName);
            addPlayerToView(oppName);
        });
    }

    public void update(String msg) {
        // method for updating UI with inGame message

    }

    private void addPlayerToView(String playerName) {
        TextView playerView = new TextView(getContext());
        playerView.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        playerView.setText(String.format("Player: %s", playerName));

        playerView.setTextSize(16);
        playerView.setTypeface(playerView.getTypeface(), Typeface.BOLD);
        playerView.setTextColor(getResources().getColor(R.color.black));

        playersContainer.addView(playerView);
    }



}