package com.example.stratego_app.connection.clients;

import android.util.Log;

import com.example.stratego_app.models.Player;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class LobbyClient implements Disposable {

    private static final String TAG = "LobbyClient";

    private static final String URL = "ws://se2-demo.aau.at:53216/ws/websocket";
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final Gson gson = new Gson();
    private List<Player> currentLobby = new ArrayList<>();
    private StompClient client;


    public void connect() {
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, URL);
        client.withClientHeartbeat(1000).withServerHeartbeat(1000);
        Disposable lifecycle = client.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case ERROR:
                            Log.e(TAG, "error", lifecycleEvent.getException());
                            break;
                        case OPENED:
                            Log.e(TAG, "opened");
                            break;
                        case CLOSED:
                            Log.e(TAG, "closed");
                            break;
                        default:
                            break;
                    }
                }));

        disposable.add(lifecycle);

        Disposable lobby = client.topic("/topic/lobby")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLobbyChange, throwable -> Log.e(TAG, "error", throwable));

        disposable.add(lobby);

        client.connect();
    }

    private void onLobbyChange(StompMessage stompMessage) {
        Log.d("stomp", stompMessage.getPayload());
        currentLobby = gson.<List<Player>>fromJson(stompMessage.getPayload(), List.class);
    }

    public void joinLobby(Player player) {
        String data = gson.toJson(player);
        client.send("/app/join", data);
    }

    public void leaveLobby(Player player) {
        String data = gson.toJson(player);
        client.send("/app/leave", data);
    }

    public List<Player> getCurrentLobby() {
        return currentLobby;
    }

    @Override
    public void dispose() {
        disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposable.isDisposed();
    }
}