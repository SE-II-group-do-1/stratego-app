package com.example.stratego_app.ui;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public class DialogFragmentLeaveGame extends DialogFragment {

    public interface ConfirmLeaveDialogListener {
        void onConfirmLeave();
        void onCancelLeave();
    }

    private ConfirmLeaveDialogListener listener;

    public DialogFragmentLeaveGame(ConfirmLeaveDialogListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setMessage("Are you sure you want to leave the game?")
                .setTitle("Confirm Leave")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onConfirmLeave();
                        }
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        if (listener != null) {
                            listener.onCancelLeave();
                        }
                        dialog.dismiss();
                    }
                });
        return builder.create();
    }
}
