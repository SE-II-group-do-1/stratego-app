package com.example.stratego_app.ui;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stratego_app.R;


public class GameFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_game_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // look up GameBoardView by ID - GameBoardView not implemented yet
        GameBoardView gameBoardView = view.findViewById(R.id.gameBoardView);
        gameBoardView.setConfigMode(true);
         /*
        place to add interaction modules for board game.
        Nature of implementation has not been decided on yet.
         */
    }

}
