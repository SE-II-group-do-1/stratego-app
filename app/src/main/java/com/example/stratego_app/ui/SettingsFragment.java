package com.example.stratego_app.ui;

import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.stratego_app.R;
import com.example.stratego_app.model.ModelService;

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
        PiecesAdapter piecesAdapter;
        super.onViewCreated(view, savedInstanceState);

        GameBoardView gameBoardView = view.findViewById(R.id.settingsGameBoardView);
        gameBoardView.setConfigMode(false);
        gameBoardView.setDisplayLowerHalfOnly(true);
        gameBoardView.setupDragListener();

        RecyclerView piecesRecyclerView = view.findViewById(R.id.piecesRecyclerView);
        piecesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));

        piecesAdapter = new PiecesAdapter(getPiecesList(), (viewHolder, position) -> {
            ClipData data = ClipData.newPlainText("", "");//include relevant piece Data here to forward when connected to ModelSession
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(viewHolder.itemView);
            viewHolder.itemView.startDrag(data, shadowBuilder, viewHolder.itemView, 0);
            gameBoardView.setCurrentDragPosition(position);  // Track the position of the currently dragged item
        });

        gameBoardView.setDropListener((success, position) -> {
            if (success) {
                piecesAdapter.removeItem(position); // Remove the item only on successful drop
            }
        });

        piecesRecyclerView.setAdapter(piecesAdapter);

        /*
         * call to fill board randomly by clicking button
         */

        Button fillBoard = view.findViewById(R.id.fillButton);
    }

    //Helper methods



    private void updateGameBoardView() {
        GameBoardView gameBoardView = getView().findViewById(R.id.settingsGameBoardView);
        if (gameBoardView != null) {
            gameBoardView.invalidate(); // Redraw the game board view to reflect new pieces
        }
    }


    // Helper method to get a list of all pieces
    private List<String> getPiecesList() {
        List<String> pieces = new ArrayList<>();

        pieces.addAll(Collections.nCopies(1, "flag"));
        pieces.addAll(Collections.nCopies(1, "marshal"));
        pieces.addAll(Collections.nCopies(1, "general"));
        pieces.addAll(Collections.nCopies(2, "colonel"));
        pieces.addAll(Collections.nCopies(3, "major"));
        pieces.addAll(Collections.nCopies(4, "captain"));
        pieces.addAll(Collections.nCopies(4, "lieutenant"));
        pieces.addAll(Collections.nCopies(4, "sergeant"));
        pieces.addAll(Collections.nCopies(5, "miner"));
        pieces.addAll(Collections.nCopies(8, "scout"));
        pieces.addAll(Collections.nCopies(1, "spy"));
        pieces.addAll(Collections.nCopies(6, "bomb"));
        return pieces;
    }


}