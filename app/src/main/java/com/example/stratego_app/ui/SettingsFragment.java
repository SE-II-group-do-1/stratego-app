package com.example.stratego_app.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.stratego_app.R;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Rank;
import com.example.stratego_app.model.SaveSetup;
import com.google.android.material.snackbar.Snackbar;

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


        piecesAdapter = new PiecesAdapter(getPiecesList(), (viewHolder, position) -> {
            ClipData data = ClipData.newPlainText("", ""); // Include relevant piece data here
            View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(viewHolder.itemView);
            viewHolder.itemView.startDragAndDrop(data, shadowBuilder, viewHolder.itemView, 0);
            gameBoardView.setCurrentDragPosition(position); // Track the position of the currently dragged item
        });

        piecesRecyclerView.setAdapter(piecesAdapter);

        modelService.clearBoardExceptLakes();
        resetPiecesInRecycleView();

        //if setup is saved. load onto field. no drag n drop
        /*if(SaveSetup.readGameSetup(getContext()) != null)
            clearPiecesInRecyclerView();*/

        gameBoardView.setDropListener((success, position) -> {
            if (success) {
                piecesAdapter.removeItem(position);
                piecesAdapter.notifyItemRemoved(position);
                piecesAdapter.notifyItemRangeChanged(position, piecesAdapter.getItemCount());
            }
        });

        Button fillBoard = view.findViewById(R.id.fillButton);
        fillBoard.setOnClickListener(v -> {
            modelService.fillBoardRandomly();
            clearPiecesInRecyclerView();

            showSnackbar(view, "Setup board fully filled!\n"+
                    "To save your setup PRESS save button. ");
        });

        Button clearBoard = view.findViewById(R.id.clearButton);
        clearBoard.setOnClickListener(v -> {
            modelService.clearBoardExceptLakes();
            resetPiecesInRecycleView();

            showSnackbar(view, "Game setup cleared!");
        });

        Button saveGameSetUp = view.findViewById(R.id.saveButton);
        saveGameSetUp.setOnClickListener(v -> {
            String username = getArguments().getString("username", "defaultUsername");
            SaveSetup.saveGameSetup(getContext(), username);

            showSnackbar(view, "Game setup successfully saved");
        });



        Button leave = view.findViewById(R.id.btnLeaveSettings);
        leave.setOnClickListener(v ->{
            //modelService.clearBoardExceptLakes();
            resetPiecesInRecycleView();
            getParentFragmentManager().popBackStack();
        });
    }

    private void resetPiecesInRecycleView() {
        piecesAdapter.setPieces(getPiecesList());
        piecesAdapter.notifyDataSetChanged(); //maybe use more specific change event here!
    }
    private void clearPiecesInRecyclerView() {
        piecesAdapter.clearPieces();
        piecesAdapter.notifyDataSetChanged();
    }

    // Helper method to get a list of all pieces
    private List<Piece> getPiecesList() {
        List<Piece> pieces = new ArrayList<>();
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.FLAG, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.MARSHAL, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.GENERAL, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(2, new Piece(Rank.COLONEL, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(3, new Piece(Rank.MAJOR, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.CAPTAIN, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.LIEUTENANT, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(4, new Piece(Rank.SERGEANT, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(5, new Piece(Rank.MINER, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(8, new Piece(Rank.SCOUT, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(1, new Piece(Rank.SPY, ModelService.getInstance().getPlayerColor())));
        pieces.addAll(Collections.nCopies(6, new Piece(Rank.BOMB, ModelService.getInstance().getPlayerColor())));

        return pieces;
    }

    @SuppressLint("RestrictedApi")
    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = 500;
        snackbarLayout.setLayoutParams(params);

        // Show the Snackbar
        snackbar.show();
    }


}