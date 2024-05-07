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

    private void notifyListeners(StompMessage message) {
        for (LobbyClientListener listener : listeners) {
            listener.onLobbyResponse(message);
        }
    }


    /**
     * Establishes connection with server and listens to topics concerning errors and
     * initial server response (only after sending join lobby request)
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

    /**
     * Called after connecting to server and receiving response. Assigns player its info
     * and starts listening to specific lobby topic with given ID. Also listens to specific
     * setup topic with given ID, for initial Board setup.
     * @param message - Map containing player info, color for game, and lobby ID
     */
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

    /**
     * Called when pressing a button to request joining a lobby to start playing.
     * @param username - sent to server to create player and get asigned to a lobby.
     *                 response is handled in onLobbyResponse()
     */
    public void joinLobby(String username) {
        String data = gson.toJson(username);
        client.send("/app/join", data).subscribe();
    }

    /**
     * Called when a button is pressed. Sends current player chosen Board layout to server,
     * after which the game can start.
     * @param board - the player defined arrangement of pieces.
     * @param player - self, the player making the request
     */
    public void sendBoard(Board board, Player player){
        String data = gson.toJson(setupToObject(player, board));
        client.send("/setup", data);
    }

    /**
     * Handles response from server after sending player-defined board (sendBoard()).
     * @param message - currently just Logs.
     */
    public void setupBoardResponse(StompMessage message){
        Log.i(TAG, message.getPayload());
    }

    /**
     * Handles all in game server responses (mostly updating Board).
     * @param message - can be "close" if other participant left, or updated position of Piece
     */
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

    /**
     * Called after a button is pressed. Sends player move to server.
     * @param y - new Y position of Piece
     * @param x - new X position of Piece
     * @param piece - Piece that was moved
     * @param initiator - player that made the move
     */
    public void sendUpdate(int y, int x, Piece piece, Player initiator){
        String data = gson.toJson(updateToObject(y,x,piece, initiator));
        client.send("/topic/lobby-"+currentLobbyID, data);
    }

    /**
     * Handles exceptions sent by server. Simply logs them.
     * @param message - exception from server.
     */
    private void handleException(StompMessage message){
        Log.e(TAG, message.getPayload());
    }

    /**
     * Called when player leaves the lobby.
     * @param player - self, for server to identify.
     */
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