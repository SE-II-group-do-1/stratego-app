package com.example.stratego_app.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.stratego_app.R;
import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.SaveSetup;


public class MainFragment extends Fragment {


    ModelService modelService = ModelService.getInstance();

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
        Button enter = view.findViewById(R.id.enterButton);
        EditText usernameEntry = view.findViewById(R.id.enterUsername);

        setButtonDisabled(startGame);
        setButtonDisabled(enter);


        startGame.setOnClickListener(v -> {

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new GameFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        //startGame.setEnabled(false);


        enter.setOnClickListener(view1 -> {
            String username = usernameEntry.getText().toString().trim();

            if (!username.isEmpty() && username != "") {
                if (SaveSetup.doesGameSetupExist(getContext())) {
                    setButtonEnabled(startGame);
                } else {
                    setButtonDisabled(startGame); // Ensure it's disabled if no setup exists
                }

                //ModelService.getInstance().Player(username, -1);//has to be updated as id is received by server!
                LobbyClient.connect();
                LobbyClient.joinLobby(username);

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

        usernameEntry.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String username = s.toString().trim();
                if (!username.isEmpty()) {
                    setButtonEnabled(enter);
                } else {
                    setButtonDisabled(enter);
                }
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });
    }

    private void setButtonDisabled(Button button) {
        button.setEnabled(false);
        button.setAlpha(0.5f); // Set transparency to indicate disabled state
        button.setTextColor(getResources().getColor(R.color.disabled_text_color));
    }

    private void setButtonEnabled(Button button) {
        button.setEnabled(true);
        button.setAlpha(1.0f); // Reset transparency to indicate enabled state
        button.setTextColor(getResources().getColor(R.color.white));
    }
}