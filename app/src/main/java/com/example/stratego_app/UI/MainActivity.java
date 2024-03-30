package com.example.stratego_app.UI;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.stratego_app.R;

public class MainActivity extends AppCompatActivity {

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
    }
}