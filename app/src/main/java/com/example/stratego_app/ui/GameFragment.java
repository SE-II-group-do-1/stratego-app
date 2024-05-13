    package com.example.stratego_app.ui;

    import android.annotation.SuppressLint;
    import android.os.Bundle;
    import androidx.annotation.NonNull;
    import androidx.annotation.Nullable;
    import androidx.fragment.app.Fragment;

    import android.os.Handler;
    import android.os.SystemClock;
    import android.view.LayoutInflater;
    import android.view.View;
    import android.view.ViewGroup;
    import android.widget.Button;
    import android.widget.TextView;

    import com.example.stratego_app.R;
    import com.example.stratego_app.model.ModelService;


    public class GameFragment extends Fragment {

        ModelService modelService = ModelService.getInstance();

        private TextView timeCounter;
        private long startTime = 0L;
        private final Handler handler = new Handler();
        private long timeInMilliseconds = 0L;
        private long timeBuff = 0L;

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

            // look up GameBoardView by ID - GameBoardView not implemented yet
            GameBoardView gameBoardView = view.findViewById(R.id.gameBoardView);
            //gameBoardView.setConfigMode(true);

            timeCounter = view.findViewById(R.id.timeCounter);
            startTimer();

            /*
              ends the game
             */
            Button btnLeaveGame = view.findViewById(R.id.leaveGameButton);
            btnLeaveGame.setOnClickListener(v -> {
                modelService.clearBoardExceptLakes(); //clears board when leaving
                getParentFragmentManager().popBackStack(); //go back to previous fragment
            });
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

    }
