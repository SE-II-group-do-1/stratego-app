    package com.example.stratego_app.ui;

    import android.annotation.SuppressLint;
    import android.app.Dialog;
    import android.graphics.Color;
    import android.os.Bundle;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;

    import androidx.fragment.app.Fragment;
    import androidx.fragment.app.FragmentManager;

    import android.os.Handler;
    import android.os.SystemClock;
    import android.view.Gravity;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.FrameLayout;
    import android.widget.TextView;

    import com.example.stratego_app.R;
    import com.example.stratego_app.model.ModelService;
    import com.google.android.material.snackbar.Snackbar;


    public class GameFragment extends Fragment implements DialogFragmentLeaveGame.ConfirmLeaveDialogListener {

        ModelService modelService = ModelService.getInstance();

        private TextView timeCounter;
        private long startTime = 0L;
        private final Handler handler = new Handler();
        private long timeInMilliseconds = 0L;
        private long timeBuff = 0L;
        private Snackbar currentSnackbar;

        private final Runnable runnable = new Runnable() {
            @SuppressLint("DefaultLocale")
            public void run() {
                timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
                long updateTime = timeBuff + timeInMilliseconds;
                int seconds = (int) (updateTime / 1000);
                int minutes = seconds / 60;
                seconds = seconds % 60;
                timeCounter.setText(String.format("%02d:%02d", minutes, seconds));
                handler.postDelayed(this, 0);
            }
        };

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {

            return inflater.inflate(R.layout.fragment_game_fragment, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            currentSnackbar = showSnackbar(view, "Game started!");

            timeCounter = view.findViewById(R.id.timeCounter);
            startTimer();

            Button btnLeaveGame = view.findViewById(R.id.leaveGameButton);
            btnLeaveGame.setOnClickListener(v -> showConfirmLeaveDialog());
        }

        private void showConfirmLeaveDialog() {
            pauseTimer();
            FragmentManager fragmentManager = getParentFragmentManager();
            DialogFragmentLeaveGame dialog = new DialogFragmentLeaveGame(this);
            dialog.show(fragmentManager, "ConfirmLeaveDialogFragment");
        }

        @Override
        public void onConfirmLeave() {
            modelService.leaveGame();
            getParentFragmentManager().popBackStack();
        }

        @Override
        public void onCancelLeave() {
            startTimer();
        }


        public void startTimer() {
            startTime = SystemClock.uptimeMillis();
            handler.postDelayed(runnable, 0);
        }

        public void pauseTimer() {
            timeBuff += timeInMilliseconds;
            handler.removeCallbacks(runnable);
        }

        @Override
        public void onDestroyView() {
            super.onDestroyView();
            pauseTimer();
        }

        @SuppressLint("RestrictedApi")
        private Snackbar showSnackbar(View view, String message) {
            Snackbar snackbar = Snackbar.make(view, "", Snackbar.LENGTH_LONG);

            LayoutInflater inflater = LayoutInflater.from(view.getContext());
            View customView = inflater.inflate(R.layout.custom_snack_layout, null);

            TextView textView = customView.findViewById(R.id.snackbar_text);
            textView.setText(message);

            @SuppressLint("RestrictedApi") Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();

            snackbarLayout.setBackgroundColor(Color.TRANSPARENT);

            // Hide default Snackbar elements
            TextView snackbarText = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_text);
            if (snackbarText != null) {
                snackbarText.setVisibility(View.INVISIBLE);
            }
            View snackbarAction = snackbarLayout.findViewById(com.google.android.material.R.id.snackbar_action);
            if (snackbarAction != null) {
                snackbarAction.setVisibility(View.INVISIBLE);
            }

            snackbarLayout.addView(customView, 0);

            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarLayout.getLayoutParams();
            params.gravity = Gravity.CENTER_HORIZONTAL;
            params.topMargin = 1250;
            snackbarLayout.setLayoutParams(params);

            snackbar.show();

            return snackbar;
        }



        @SuppressLint("RestrictedApi")
        private void updateSnackbarText(Snackbar snackbar, String message) {
            Snackbar.SnackbarLayout snackbarLayout = (Snackbar.SnackbarLayout) snackbar.getView();
            TextView textView = snackbarLayout.findViewById(R.id.snackbar_text);
            textView.setText(message);
        }


    }
