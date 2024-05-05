package com.example.stratego_app.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.stratego_app.R;
import com.example.stratego_app.connection.LobbyClient;


public class MainFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Button settingsButton = view.findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new SettingsFragment());
            fragmentTransaction.addToBackStack(null);//makes transaction reversible "back" button in app
            fragmentTransaction.commit();
        });

        Button startGame = view.findViewById(R.id.startGame);
        startGame.setOnClickListener(v -> {
            MockSessionService.clearUsernames();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new GameFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

        Button enter = view.findViewById(R.id.enterButton);
        enter.setOnClickListener(view1 -> {
            EditText usernameEntry = view.findViewById(R.id.enterUsername);
            String username = usernameEntry.getText().toString().trim();

            if (!username.isEmpty()) {

                //Player player = new Player(2, username);
                LobbyClient lc = new LobbyClient();
                lc.connect();
                lc.joinLobby(username);

                // Navigate to LobbyFragment
                LobbyFragment lobbyFragment = new LobbyFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, lobbyFragment); // Use the correct ID
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            usernameEntry.setText("");
        });

    }
}