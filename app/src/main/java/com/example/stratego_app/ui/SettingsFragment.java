package com.example.stratego_app.ui;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

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
    private Button gameSetUp;
    private Button fillBoard;
    private Button clearBoard;
    private Button leave;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initializeUIComponents(view);

        fillBoard.setOnClickListener(v -> {
            modelService.fillBoardRandomly();
            clearPiecesInRecyclerView();

            if (modelService.isSetupComplete()) {
                setButtonEnabled(gameSetUp);
            } else {
                setButtonDisabled(gameSetUp);
            }

            showSnackbar(view, "Battle Formation Set!\nTo save your setup, PRESS save button.");
        });


        clearBoard.setOnClickListener(v -> {
            modelService.clearBoardExceptLakes();
            resetPiecesInRecycleView();

            if (modelService.isSetupComplete()) {
                setButtonEnabled(gameSetUp);
            } else {
                setButtonDisabled(gameSetUp);
            }

            showSnackbar(view, "Setup cleared");
        });


        gameSetUp.setEnabled(modelService.isSetupComplete());
        gameSetUp.setOnClickListener(v -> {
            if (modelService.isSetupComplete()) {
                //String username = getArguments().getString("username", "defaultUsername");
                if (SaveSetup.saveGameSetup(getContext())) {
                    showSnackbar(view, "Formation Locked.\nSetup successfully saved!");
                } else {
                    showSnackbar(view, "Error saving setup.");
                }
            } else {
                showSnackbar(view, "Setup is incomplete. Please place all pieces.");
            }
        });



        leave = view.findViewById(R.id.btnLeaveSettings);
        leave.setOnClickListener(v ->{
            resetPiecesInRecycleView();
            getParentFragmentManager().popBackStack();
        });
    }

    private void initializeUIComponents(View view){
        gameSetUp = view.findViewById(R.id.saveButton);
        fillBoard = view.findViewById(R.id.fillButton);
        clearBoard = view.findViewById(R.id.clearButton);
        setButtonDisabled(gameSetUp);

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

        gameBoardView.setDropListener((success, position) -> {
            if (success) {
                piecesAdapter.removeItem(position);
                piecesAdapter.notifyItemRemoved(position);
                piecesAdapter.notifyItemRangeChanged(position, piecesAdapter.getItemCount());
                if (modelService.isSetupComplete()) {
                    setButtonEnabled(gameSetUp);
                } else {
                    setButtonDisabled(gameSetUp);
                }
            }
        });
    }

    private void resetPiecesInRecycleView() {
        piecesAdapter.setPieces(getPiecesList());
        piecesAdapter.notifyDataSetChanged();
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
        Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_INDEFINITE);

        LayoutInflater inflater = LayoutInflater.from(view.getContext());
        View customView = inflater.inflate(R.layout.custom_snack_layout, null);

        TextView textView = customView.findViewById(R.id.snackbar_text);
        textView.setText(message);

        Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

        snackbarLayout.setBackgroundColor(Color.TRANSPARENT);

        TextView snackbarText = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
        if (snackbarText != null) {
            snackbarText.setVisibility(View.INVISIBLE);
        }

        snackbarLayout.addView(customView, 0);

        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        params.topMargin = 300;
        snackbarLayout.setLayoutParams(params);

        snackbar.show();

        long durationInSeconds = 1;
        new Handler().postDelayed(snackbar::dismiss, durationInSeconds * 1000L); //enables auto-dismiss after 1 second
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