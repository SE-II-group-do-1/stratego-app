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
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Piece;
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

        /*
          Button SETTINGS opens up the settings editor to set the individual gameboard
         */
        Button settingsButton = view.findViewById(R.id.settings);
        settingsButton.setOnClickListener(v -> {
            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new SettingsFragment());
            fragmentTransaction.addToBackStack(null);//makes transaction reversible "back" button in app
            fragmentTransaction.commit();
        });

        /*
          Button STARTGAME starts e new game of Stratego with a two-player game set-up
         */
        Button startGame = view.findViewById(R.id.startGame);
        startGame.setOnClickListener(v -> {

            // Try to read the saved game setup
            Piece[][] setup = SaveSetup.readGameSetup(getContext());
            if (setup != null) {
                // Assuming ModelService can apply a board setup
                modelService.getGameBoard().setBoard(setup);
            }

            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, new GameFragment());
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        });
        //startGame.setEnabled(false); (deactivate startGameButton when everything else works)

        /*
          Button ENTER sends the Username to the Server and starts a new game session
         */

        Button enter = view.findViewById(R.id.enterButton);
        enter.setOnClickListener(view1 -> {
            EditText usernameEntry = view.findViewById(R.id.enterUsername);
            String username = usernameEntry.getText().toString().trim();

            if (!username.isEmpty()) {
                //startGame.setEnabled(true); (activate startGameButton when everything else works)

                //ModelService.getInstance().Player(username, -1);//has to be updated as id is received by server!
                LobbyClient lc = LobbyClient.getInstance();
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