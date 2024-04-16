package com.example.stratego_app.ui;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.stratego_app.R;
import com.example.stratego_app.connection.clients.LobbyClient;

public class MainActivity extends AppCompatActivity {
    private LobbyClient lc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Container for UI Main fragment(=startPage) to be displayed and executed*/
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.fragment_container, new MainFragment())
                    .commit();
        }

        lc = new LobbyClient();
        lc.connect();
    }

    public LobbyClient getLobbyClient() {
        return lc;
    }
}