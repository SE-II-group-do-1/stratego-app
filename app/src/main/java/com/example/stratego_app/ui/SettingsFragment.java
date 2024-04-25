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

    ModelService modelService = ModelService.getInstance();
    PiecesAdapter piecesAdapter;


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
        gameBoardView.setupDragListener();

        RecyclerView piecesRecyclerView = view.findViewById(R.id.piecesRecyclerView);
        piecesRecyclerView.setLayoutManager(new GridLayoutManager(getContext(), 6));

        // Initialize the class field directly, removing the local declaration
        piecesAdapter = new PiecesAdapter(getPiecesList(), (viewHolder, position) -> {
            ClipData data = ClipData.newPlainText("", ""); // Include relevant piece data here
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(viewHolder.itemView);
            viewHolder.itemView.startDrag(data, shadowBuilder, viewHolder.itemView, 0);
            gameBoardView.setCurrentDragPosition(position); // Track the position of the currently dragged item
        });

        piecesRecyclerView.setAdapter(piecesAdapter);

        gameBoardView.setDropListener((success, position) -> {
            if (success) {
                piecesAdapter.removeItem(position); // Remove the item only on successful drop
            }
        });

        Button fillBoard = view.findViewById(R.id.fillButton);
        fillBoard.setOnClickListener(v -> {
            modelService.fillBoardRandomly();
            clearPiecesInRecyclerView();
        });

        Button clearBoard = view.findViewById(R.id.clearButton);
        clearBoard.setOnClickListener(v -> {
            modelService.clearBoardExceptLakes();
            resetPiecesInRecycleView();
        });

        Button saveGameSetUp = view.findViewById(R.id.saveButton);
        saveGameSetUp.setOnClickListener(v -> {
            modelService.saveGameSetup();

        });
    }

    //Helper methods

    private void resetPiecesInRecycleView() {
        piecesAdapter.setPieces(getPiecesList());
        piecesAdapter.notifyDataSetChanged();
    }
    private void clearPiecesInRecyclerView() {
        piecesAdapter.clearPieces();
        piecesAdapter.notifyDataSetChanged();
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