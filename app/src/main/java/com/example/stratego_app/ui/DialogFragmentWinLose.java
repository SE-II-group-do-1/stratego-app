package com.example.stratego_app.ui;


import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.stratego_app.model.GameState;
import com.example.stratego_app.model.ModelService;

public class DialogFragmentWinLose extends DialogFragment {

    public interface WinLoseDialogListener {
        void onAcceptLeave();
    }

    private WinLoseDialogListener listener;

    public DialogFragmentWinLose(WinLoseDialogListener listener){
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        String message = ModelService.getInstance().getGameState() == GameState.WIN? "You won the game!" : "You lost the game!";
        builder.setMessage(message)
                .setTitle("Confirm Leave")
                .setPositiveButton("Leave", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onAcceptLeave();
                        }
                    }
                });
        return builder.create();
    }
}
