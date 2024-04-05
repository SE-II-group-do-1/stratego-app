package com.example.stratego_app.ui;

import android.content.Context;
import android.content.SharedPreferences;
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

import java.util.HashSet;
import java.util.Set;

public class MainFragment extends Fragment {

    private static final String PLAYER_USERNAME = "usernames";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
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
            clearUsernames();

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new GameFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        Button enter = view.findViewById(R.id.enterButton);
        enter.setOnClickListener(view1 -> {
            EditText usernameEntry = view.findViewById(R.id.enterUsername);
            String username = usernameEntry.getText().toString();

            saveUsername(username);
            usernameEntry.setText("");

            LobbyFragment lobbyFragment = new LobbyFragment();

            // Create a bundle to pass the username
            Bundle args = new Bundle();
            args.putString("username", username);
            lobbyFragment.setArguments(args);

            FragmentManager fragmentManger = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManger.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainerView, lobbyFragment); // Ensure you use the correct container ID
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });

    }

    private void saveUsername(String username) {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("LobbyPrefs", Context.MODE_PRIVATE);
        Set<String> usernames = new HashSet<>(sharedPrefs.getStringSet(PLAYER_USERNAME, new HashSet<>()));
        usernames.add(username);
        sharedPrefs.edit().putStringSet(PLAYER_USERNAME, usernames).commit();
    }

    //this method needs to be adapted when the Server is ready.
    private void clearUsernames() {
        SharedPreferences sharedPrefs = getActivity().getSharedPreferences("LobbyPrefs", Context.MODE_PRIVATE);
        sharedPrefs.edit().remove(PLAYER_USERNAME).apply();
    }
}