package com.example.stratego_app.ui;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.stratego_app.R;
import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.model.GameState;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.ObserverModelService;
import com.example.stratego_app.model.SaveSetup;


public class MainFragment extends Fragment implements ObserverModelService{


    private static final String TAG = "main";
    ModelService modelService = ModelService.getInstance();
    private String username;

    private boolean isGameFragmentLoaded = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_fragment, container, false);
    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LobbyClient.connect();
        ModelService.subscribe(this);

        Button settingsButton = view.findViewById(R.id.settings);
        Button enter = view.findViewById(R.id.enterButton);
        EditText usernameEntry = view.findViewById(R.id.enterUsername);

        setButtonDisabled(enter);




        settingsButton.setOnClickListener(v -> {
            username = usernameEntry.getText().toString().trim();
            Bundle bundle = new Bundle();
            bundle.putString("username", username);

            SettingsFragment settingsFragment = new SettingsFragment();
            settingsFragment.setArguments(bundle);

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, settingsFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        enter.setOnClickListener(view1 -> {
            username = usernameEntry.getText().toString().trim();
            if (!username.isEmpty()) {
                if(!SaveSetup.readGameSetup(getContext())) {
                    ModelService.getInstance().fillBoardRandomly();
                    Log.i("SaveSetup", "random board");
                }

                LobbyClient.joinLobby(username);

                LobbyFragment lobbyFragment = new LobbyFragment();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.fragmentContainerView, lobbyFragment);
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.commit();
            }
            usernameEntry.setText("");
        });



        usernameEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                username = s.toString().trim();
                if (!username.isEmpty()) {
                    setButtonEnabled(enter);
                } else {
                    setButtonDisabled(enter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

    }

    @Override
    public void update() {
        Log.d(TAG, "Observer update() called. Current game state: " + modelService.getGameState());
        if (modelService.getGameState() == GameState.INGAME && !isGameFragmentLoaded) {
            Log.d(TAG, "Game state is INGAME. Navigating to GameFragment.");
            navigateToGameFragment();
        } else {
            Log.d(TAG, "Game state is not INGAME. Current state: " + modelService.getGameState());
        }
    }
    private void navigateToGameFragment() {
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .setReorderingAllowed(true)
                .replace(R.id.fragment_container, GameFragment.class, null, "gamefragment")
                .addToBackStack(null)
                .commit();
        isGameFragmentLoaded = true;
    }

    private void setButtonDisabled(Button button) {
        button.setEnabled(false);
        button.setAlpha(0.5f);
        button.setTextColor(getResources().getColor(R.color.disabled_text_color));
    }

    private void setButtonEnabled(Button button) {
        button.setEnabled(true);
        button.setAlpha(1.0f);
        button.setTextColor(getResources().getColor(R.color.black));
    }
}