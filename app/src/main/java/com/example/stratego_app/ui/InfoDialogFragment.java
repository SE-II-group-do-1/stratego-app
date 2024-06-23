package com.example.stratego_app.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.stratego_app.R;


public class InfoDialogFragment extends DialogFragment {

    public static InfoDialogFragment newInstance() {
        return new InfoDialogFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_info_dialog, container, false);

        GridLayout piecesGrid = view.findViewById(R.id.pieces_grid);
        Button closeButton = view.findViewById(R.id.close_button);
        TextView pieceStrengthInfo = view.findViewById(R.id.piece_strength_info);
        TextView specialRules = view.findViewById(R.id.special_rules);

        int[] pieceImages = {
                R.drawable.flag, R.drawable.marshal, R.drawable.general, R.drawable.colonel, R.drawable.major, R.drawable.captain, R.drawable.lieutenant, R.drawable.sergeant, R.drawable.miner,R.drawable.scout, R.drawable.spy,
        };

        String[] pieceNames = {
                "Flag", "Marshal", "General", "Colonel", "Major", "Captain", "Lieutenant", "Sergeant", "Miner", "Scout", "Spy",
        };
        // Set strengths and defeats info
        pieceStrengthInfo.setText("Piece Strengths:\nMarshal (10), General (9), Colonel(8), Major(7), Captain(6), Lieutenant(5), Sergent(4), Miner(3), Scout(2), Spy(1) \n\nDefeats:\nMarshal > General, ...");

        // Set special rules
        specialRules.setText("Special Rules:\n1. Only the Miner can defeat the bomb, everyone else loses\n2. The Spy can defeat the marshal if he attacks first\n3. Every moving piece can capture the flag and win the game");

        // Add pieces dynamically to the GridLayout
        addPiecesToGridLayout(piecesGrid, pieceImages, pieceNames, inflater);

        // Set the close button listener
        closeButton.setOnClickListener(v -> dismiss());

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        }
    }

    private void addPiecesToGridLayout(GridLayout gridLayout, int[] pieceImages, String[] pieceNames, LayoutInflater inflater) {
        for (int i = 0; i < pieceImages.length; i++) {
            LinearLayout itemLayout = (LinearLayout) inflater.inflate(R.layout.item_pieces, gridLayout, false);
            ImageView pieceImage = itemLayout.findViewById(R.id.pieceImageView);
            TextView pieceName = itemLayout.findViewById(R.id.pieceNameTextView);

            pieceImage.setImageResource(pieceImages[i]);
            pieceName.setText(pieceNames[i]);

            gridLayout.addView(itemLayout);
        }
    }
}