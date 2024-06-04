package com.example.stratego_app.ui;

import android.os.Bundle;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

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
    private String username;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        LobbyClient.connect();

        Button settingsButton = view.findViewById(R.id.settings);
        Button startGame = view.findViewById(R.id.startGame);
        Button enter = view.findViewById(R.id.enterButton);
        EditText usernameEntry = view.findViewById(R.id.enterUsername);

        setButtonDisabled(enter);
        setButtonDisabled(startGame);


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

        /*LobbyViewModel lobbyViewModel = new ViewModelProvider(requireActivity()).get(LobbyViewModel.class);
        lobbyViewModel.getNumberOfPlayers().observe(getViewLifecycleOwner(), numberOfPlayers -> {
            if (numberOfPlayers >= 2) {
                setButtonEnabled(startGame);
            } else {
                setButtonDisabled(startGame);
            }
        });*/
        setButtonEnabled(startGame);
        startGame.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new GameFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });


        enter.setOnClickListener(view1 -> {
            username = usernameEntry.getText().toString().trim();
            if (!username.isEmpty()) {
                if (!SaveSetup.doesGameSetupExist(getContext(), username)) {//no setup exists in settings editor
                    modelService.fillBoardRandomly();
                    SaveSetup.saveGameSetup(getContext(), username);
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

    private void setButtonDisabled(Button button) {
        button.setEnabled(false);
        button.setAlpha(0.5f);
        button.setTextColor(getResources().getColor(R.color.disabled_text_color));
    }

    private void setButtonEnabled(Button button) {
        button.setEnabled(true);
        button.setAlpha(1.0f);
        button.setTextColor(getResources().getColor(R.color.white));
    }
}