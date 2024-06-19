package com.example.stratego_app.connection;

import android.util.Log;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Color;
import com.example.stratego_app.model.GameState;
import com.example.stratego_app.model.ModelService;
import com.example.stratego_app.model.Player;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.Objects;

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
    private static final CompositeDisposable disposable = new CompositeDisposable();
    private static Gson gson = new GsonBuilder().serializeNulls().create();

    private static int currentLobbyID;

    private static String username;
    private static StompClient client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, URL);

    private static Disposable reply;
    private static Disposable errors;
    private static Disposable currentLobby;


    /**
     * Establishes connection with server and listens to topics concerning errors and
     * initial server response (only after sending join lobby request)
     */
    public static void connect() {

        String errorMsg = "error";
        client.withClientHeartbeat(1000).withServerHeartbeat(1000);
        Disposable lifecycle = client.lifecycle()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((lifecycleEvent -> {
                    switch (lifecycleEvent.getType()) {
                        case ERROR:
                            Log.e(TAG, errorMsg, lifecycleEvent.getException());
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
                .subscribe(LobbyClient::onLobbyResponse, throwable -> Log.e(TAG, errorMsg, throwable));

        errors = client.topic("/user/topic/errors")
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(LobbyClient::handleException, throwable -> Log.e(TAG, errorMsg, throwable));

        disposable.add(reply);
        disposable.add(errors);

        client.connect();
    }

    /**
     * Called when pressing a button to request joining a lobby to start playing.
     * @param u - sent to server to create player and get asigned to a lobby.
     *                 response is handled in onLobbyResponse()
     */
    public static void joinLobby(String u) {
        username = u;
        Log.i(TAG, username);
        String data = gson.toJson(username);
        client.send("/app/join", data).subscribe();
    }

    /**
     * Called after connecting to server and receiving response. Assigns player its info
     * and starts listening to specific lobby topic with given ID. Also listens to specific
     * setup topic with given ID, for initial Board setup.
     * @param payload - Map containing player info, color for game, and lobby ID
     */
    private static void onLobbyResponse(StompMessage payload) {
        try{
            LobbyMessage message = gson.fromJson(payload.getPayload(), LobbyMessage.class);
            //get vals
            currentLobbyID = message.getLobbyID();
            Player blue = message.getBlue();
            Player red = message.getRed();

            Log.i(TAG, message.toString());

            // if username is not one of the returned, just ignore
            if(!(Objects.equals(blue.getUsername(), username)) && !(Objects.equals(red.getUsername(), username))) return;
            //assign values
            if(Objects.equals(blue.getUsername(), username)){
                ModelService.getInstance().setPlayerColor(Color.BLUE);
                ModelService.getInstance().Player(blue);
                ModelService.getInstance().Opponent(red);
            } else{
                ModelService.getInstance().setPlayerColor(Color.RED);
                ModelService.getInstance().Player(red);
                ModelService.getInstance().Opponent(blue);
            }

            ModelService.getInstance().setGameState(GameState.INGAME);
            Log.i(TAG, "after assigning vals");

            //unsub from reply
            disposable.remove(reply);
            reply.dispose();

            //sub to lobby
            currentLobby = client.topic("/topic/lobby-"+currentLobbyID)
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(LobbyClient::handleUpdate, throwable -> Log.e(TAG, "error subscribing to lobby", throwable));
            disposable.add(currentLobby);
            Log.i(TAG, "subbed to currentLobby"+currentLobby);

            //send baord setup
            sendBoardSetup(ModelService.checkForRotation(ModelService.getInstance().getGameBoard()));

        }
        catch (Exception e){
            Log.e(TAG, e.toString());
        }
    }

    /**
     * Called from onLobbyResponse. Automatically sends the Player's Board setup to the
     * Server /setup endpoint. When both Players sent their setup, the Server sends a
     * response to handleUpdateMessage, setting the board and allowing the game to start.
     * @param b - the player's board.
     */
    public static void sendBoardSetup(Board b){
        int id = ModelService.getInstance().getCurrentPlayer().getId();
        UpdateMessage updateMessage = new UpdateMessage();

        updateMessage.setInitiator(id);
        updateMessage.setLobbyID(currentLobbyID);
        updateMessage.setBoard(b);

        String data = gson.toJson(updateMessage);
        client.send("/app/setup", data).subscribe();

    }

    /**
     * Called from ModelService after a button is pressed. Sends player move to server.
     * @param b - updated Board, id is sent implicitly
     */
    public static void sendUpdate(Board b){
        int id = ModelService.getInstance().getCurrentPlayer().getId();

        UpdateMessage updateMessage = new UpdateMessage();

        updateMessage.setInitiator(id);
        updateMessage.setLobbyID(currentLobbyID);
        updateMessage.setBoard(b);
        updateMessage.setCheat(false);
        updateMessage.setCheck(false);

        String data = gson.toJson(updateMessage);

        client.send("/app/update", data).subscribe();
        Log.i(TAG, "message sent");
    }

    /**
     * Handles all in game server responses (mostly updating Board).
     * @param message - can be "close" if other participant left, or updated position of Piece
     */
    private static void handleUpdate(StompMessage message){
        try {
            //parse message
            UpdateMessage u = gson.fromJson(message.getPayload(), UpdateMessage.class);
            Board b = u.getBoard();
            Color winner = u.getWinner();
            boolean close = u.getClose();
            if(close){
                ModelService.getInstance().setGameState(GameState.DONE);
                return;
            }

                //commit changes
            ModelService.getInstance().updateBoard(b);
            ModelService.getInstance().checkWin(winner);

            Log.i(TAG, message.toString());
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
    }


    /**
     * Handles exceptions sent by server. Simply logs them.
     * @param message - exception from server.
     */
    private static void handleException(StompMessage message){
        Log.e("Server Error", message.getPayload());
    }

    /**
     * Called when player leaves the lobby.
     * @param id - self, for server to identify.
     */
    public static void leaveLobby(int id) {
        String data = gson.toJson(id);
        client.send("/app/leave", data).subscribe();
        disposable.remove(currentLobby);
        currentLobby.dispose();

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