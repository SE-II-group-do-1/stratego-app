package com.example.stratego_app.ui;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.stratego_app.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


public class SettingsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        GameBoardView gameBoardView = view.findViewById(R.id.settingsGameBoardView);
        gameBoardView.setConfigMode(false);
        gameBoardView.setDisplayLowerHalfOnly(true);


        RecyclerView piecesRecyclerView = view.findViewById(R.id.piecesRecyclerView);
        int numberOfColumns = 6;
        piecesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), numberOfColumns));

        PiecesAdapter piecesAdapter = new PiecesAdapter(getPiecesList());
        piecesRecyclerView.setAdapter(piecesAdapter);

    }
    // Helper method to get a list of your pieces
    private List<String> getPiecesList() {
        List<String> pieces = new ArrayList<>();

        pieces.addAll(Collections.nCopies(1, "flag"));
        pieces.addAll(Collections.nCopies(1, "marshall"));
        pieces.addAll(Collections.nCopies(1, "general"));
        pieces.addAll(Collections.nCopies(2, "colonel"));
        pieces.addAll(Collections.nCopies(3, "major"));
        pieces.addAll(Collections.nCopies(4, "captain"));
        pieces.addAll(Collections.nCopies(4, "lieutnant"));
        pieces.addAll(Collections.nCopies(4, "sergeant"));
        pieces.addAll(Collections.nCopies(5, "miner"));
        pieces.addAll(Collections.nCopies(8, "scout"));
        pieces.addAll(Collections.nCopies(1, "spion"));
        pieces.addAll(Collections.nCopies(6, "bomb"));
        return pieces;
    }


}