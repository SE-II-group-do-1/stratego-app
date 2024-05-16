package com.example.stratego_app.connection;

import static com.example.stratego_app.connection.ToMap.updateToObject;

import android.util.Log;
import android.view.Display;

import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Color;
import com.example.stratego_app.model.GameState;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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

    private String username;
    private StompClient client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, URL);

    Disposable reply;
    Disposable errors;
    Disposable currentLobby;

    public static synchronized LobbyClient getInstance(){
        if(instance == null){
            return new LobbyClient();
        }
        else{
            return instance;
        }
    }

    /**
     * Establishes connection with server and listens to topics concerning errors and
     * initial server response (only after sending join lobby request)
     */
    public void connect() {
        this.client.withClientHeartbeat(1000).withServerHeartbeat(1000);
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


        reply = this.client.topic("/topic/reply")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(this::onLobbyResponse, throwable -> Log.e(TAG, "error", throwable));


        errors = this.client.topic("/topic/errors")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleException, throwable -> Log.e(TAG, "error", throwable));

        disposable.add(reply);
        disposable.add(errors);

        this.client.connect();
    }

    /**
     * Called when pressing a button to request joining a lobby to start playing.
     * @param username - sent to server to create player and get asigned to a lobby.
     *                 response is handled in onLobbyResponse()
     */
    public void joinLobby(String username) {
        this.username = username;
        Log.i(TAG, username);
        String data = gson.toJson(username);
        client.send("/app/join", data).subscribe();
    }

    /**
     * Called after connecting to server and receiving response. Assigns player its info
     * and starts listening to specific lobby topic with given ID. Also listens to specific
     * setup topic with given ID, for initial Board setup.
     * @param message - Map containing player info, color for game, and lobby ID
     */
    private void onLobbyResponse(StompMessage message) {
        Log.i(TAG, message.getPayload());
        Map<String, Object> payload = ToMap.parseMessage(message);
        try{
            //parse message
            Double id = (Double) payload.get("id");
            Double idRed = (Double) payload.get("playerRedID");
            Double idBlue = (Double) payload.get("playerBlueID");
            currentLobbyID = id.intValue();
            String playerRed = (String) payload.get("playerRedName");
            String playerBlue = (String) payload.get("playerBlueName");

            Log.i(TAG, payload.toString());

            //check who is self
            Player selfInfo;
            Player opponent;
            Color color;
            if(Objects.equals(playerRed, this.username)){
                selfInfo = new Player(playerRed, idRed.intValue());
                opponent = new Player(playerBlue, idBlue.intValue());
                color = Color.RED;
            }
            else{
                selfInfo = new Player(playerBlue, idBlue.intValue());
                opponent = new Player(playerRed, idRed.intValue());
                color = Color.BLUE;
            }

            //TODO: ModelService doesnt need a Player class. keep values in normal form!
            //  leave for now. maybe in sprint 3!!!!!

            //update info in ModelService
            ModelService.getInstance().Player(selfInfo);
            ModelService.getInstance().setPlayerColor(color);
            ModelService.getInstance().Opponent(opponent);
            ModelService.getInstance().setGameState(GameState.INGAME);

            //unsub from reply
            disposable.remove(reply);
            reply.dispose();

            //sub to lobby
            this.currentLobby = this.client.topic("/topic/lobby-"+currentLobbyID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(this::handleUpdate, throwable -> Log.e(TAG, "error subscribing to lobby", throwable));
            disposable.add(this.currentLobby);

        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Called from ModelService after a button is pressed. Sends player move to server.
     * @param b - updated Board, id is sent implicitly
     */
    public void sendUpdate(Board b){
        int id = ModelService.getInstance().getCurrentPlayer().getId();
        String data = gson.toJson(updateToObject(b,id));
        client.send("/topic/lobby-"+currentLobbyID, data).subscribe();
        Log.i(TAG, "message sent");
    }

    /**
     * Handles all in game server responses (mostly updating Board).
     * @param message - can be "close" if other participant left, or updated position of Piece
     */
    private void handleUpdate(StompMessage message){
        if(message.getPayload().equals("close")){
            ModelService.getInstance().setGameState(GameState.DONE); // u won, other person gave up
            Log.i(TAG, "Opponent left lobby");
        }
        else {
            try {
                //parse message
                Board b = (Board) ToMap.parseMessage(message);

                //commit changes
                ModelService.getInstance().updateBoard(b);

                Log.i(TAG, message.toString());
            } catch (Exception e) {
                Log.e(TAG, e.toString());
            }
        }
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
     * @param id - self, for server to identify.
     */
    public void leaveLobby(int id) {
        String data = gson.toJson(id);
        this.client.send("/app/leave", data).subscribe();
        //disposable.remove(this.currentLobby);
        //currentLobby.dispose();
        ModelService.getInstance().setGameState(GameState.DONE);
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