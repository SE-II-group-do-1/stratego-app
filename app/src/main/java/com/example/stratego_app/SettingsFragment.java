package com.example.stratego_app;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import android.view.View;


public class SettingsFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        /*ToDo: implement GamBoardView*/
        //GameBoardView is also used in GameFragment

        GameBoardView gameBoardView = view.findViewById(R.id.settingsGameBoardView);
        //gameBoardView.setConfigurationMode(true);

        /*
        place to add setup for board game. Nature of implementation has not been decided on.
         */

    }

}