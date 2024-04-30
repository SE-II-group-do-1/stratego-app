package com.example.stratego_app.connection.clients;

import static java.util.Map.entry;

import android.util.Log;

import com.example.stratego_app.model.pieces.Piece;
import com.example.stratego_app.models.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private int currentLobbyID;
    private StompClient client;

    Disposable reply;
    Disposable errors;
    Disposable currentLobby;

    private final List<LobbyClientListener> listeners = new ArrayList<>();

    public void registerListener(LobbyClientListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(LobbyClientListener listener) {
        listeners.remove(listener);
    }


    /**
     * TODO:
     * - determine payload -> HashMap
     * - /topic/lobby -> /topic/reply
     * - impl updateLobby
     * - impl setBoard
     * - connect to UI
     * - adapt LobbyClientListener Interface
     * - sequence diagram
     * - test
     * - docs
     */

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


        reply = client.topic("/topic/reply")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLobbyResponse, throwable -> Log.e(TAG, "error", throwable));


        errors = client.topic("/topic/errors")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleException, throwable -> Log.e(TAG, "error", throwable));

        disposable.add(reply);
        disposable.add(errors);

        client.connect();
    }
    private void onLobbyResponse(StompMessage stompMessage) {
        Log.d("stomp", stompMessage.getPayload());
        currentLobbyID = Integer.parseInt(stompMessage.getPayload());
        //subscribe to assigned lobby
        currentLobby = client.topic("/topic/lobby-"+currentLobbyID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateLobby, throwable -> Log.e(TAG, "error", throwable));
        disposable.add(currentLobby);
    }

    public void joinLobby(Player player) {
        String data = gson.toJson(player);
        client.send("/app/join", data).subscribe();
    }

    private void updateLobby(StompMessage message){

    }

    public void sendUpdate(int y, int x, Piece piece, Player initiator){
        String data = gson.toJson(updateToObject(y,x,piece, initiator));
        client.send("/topic/lobby-"+currentLobbyID, data);
    }

    private void handleException(StompMessage message){
        Log.e(TAG, message.getPayload());
    }

    public void leaveLobby(Player player) {
        String data = gson.toJson(leaveToObject(player));
        client.send("/app/leave", data);
        disposable.remove(currentLobby);
    }

    public int getCurrentLobby() {
        return currentLobbyID;
    }

    @Override
    public void dispose() {
        disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposable.isDisposed();
    }

    private Map<String, Object> updateToObject(int y, int x, Piece piece, Player initiator){
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("y", y);
        toReturn.put("x", x);
        toReturn.put("piece", piece);
        toReturn.put("initiator", initiator);
        return toReturn;
    }

    private Map<String, Object> leaveToObject(Player player){
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("id", currentLobbyID);
        toReturn.put("player", player);
        return toReturn;
    }
}