package com.example.stratego_app.connection;

import static com.example.stratego_app.connection.ToMap.leaveToObject;
import static com.example.stratego_app.connection.ToMap.setupToObject;
import static com.example.stratego_app.connection.ToMap.updateToObject;

import android.util.Log;

import com.example.stratego_app.model.pieces.Board;
import com.example.stratego_app.model.pieces.Piece;
import com.example.stratego_app.models.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
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

    private static LobbyClient instance;

    private static final String TAG = "LobbyClient";

    private static final String URL = "ws://se2-demo.aau.at:53216/ws/websocket";
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final Gson gson = new Gson();
    private int currentLobbyID;
    private StompClient client;

    Disposable reply;
    Disposable setup;
    Disposable errors;
    Disposable currentLobby;



    public static LobbyClient getInstance(){
        if(instance == null){
            return new LobbyClient();
        }
        else{
            return instance;
        }
    }

    private final List<LobbyClientListener> listeners = new ArrayList<>();

    public void registerListener(LobbyClientListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(LobbyClientListener listener) {
        listeners.remove(listener);
    }


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
    private void onLobbyResponse(StompMessage message) {
        Log.d("stomp", message.getPayload());
        Map<String,Object> payload = gson.<Map<String,Object>>fromJson(message.getPayload(),
                new TypeToken<Map<String, Object>>() {}.getType());
        try{
            currentLobbyID = (Integer) payload.get("id");
            String color = (String) payload.get("color");
            Player selfInfo = (Player) payload.get("player");
            Log.i(TAG, payload.toString());
        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
        //assign player selfInfo and color
        //subscribe to assigned lobby and setup
        currentLobby = client.topic("/topic/lobby-"+currentLobbyID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::updateLobby, throwable -> Log.e(TAG, "error subscribing to lobby", throwable));
        disposable.add(currentLobby);

        setup = client.topic("/topic/setup-"+currentLobbyID)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::setupBoardResponse, throwable -> Log.e(TAG, "error subscribing to setup", throwable));
    }

    public void joinLobby(String username) {
        String data = gson.toJson(username);
        client.send("/app/join", data).subscribe();
    }

    public void sendBoard(Board board, Player player){
        String data = gson.toJson(setupToObject(player, board));
        client.send("/setup", data);
    }

    public void setupBoardResponse(StompMessage message){
        Log.i(TAG, message.getPayload());
    }

    private void updateLobby(StompMessage message){
        if(message.getPayload().equals("close")){
            Log.i(TAG, "Opponent left lobby");
        }
        else {
            try {
                Map<String, Object> payload = gson.<Map<String, Object>>fromJson(message.getPayload(),
                        new TypeToken<Map<String, Object>>() {
                        }.getType());
                Integer y = (Integer) payload.get("y");
                Integer x = (Integer) payload.get("x");
                Piece piece = (Piece) payload.get("piece");
                Log.i(TAG, payload.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
        //connect to Model

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

}