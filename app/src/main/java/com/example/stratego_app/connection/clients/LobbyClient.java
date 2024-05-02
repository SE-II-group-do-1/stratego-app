package com.example.stratego_app.connection.clients;

import android.util.Log;

import com.example.stratego_app.models.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class LobbyClient implements LobbyClientI, Disposable {

    private static final String TAG = "LobbyClient";
    private static final String URL = "ws://se2-demo.aau.at:53216/ws/websocket";
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final Gson gson = new Gson();
    private List<Player> currentLobby = new ArrayList<>();
    private StompClient client;

    private final List<LobbyClientListener> listeners = new ArrayList<>();

    public void registerListener(LobbyClientListener listener) {
        listeners.add(listener);
    }

    public void unregisterListener(LobbyClientListener listener) {
        listeners.remove(listener);
    }

    private void notifyListeners(List<Player> newLobby) {
        for (LobbyClientListener listener : listeners) {
            listener.onLobbyUpdated(newLobby);
        }
    }


    /**
     * Subscribe to a topic
     *
     * @param client   the stomp client
     * @param topic    the topic to subscribe to
     * @param consumer the consumer to handle the messages
     * @return the disposable
     */
    private static Disposable subscribeTo(StompClient client, String topic, Consumer<? super StompMessage> consumer) {
        return client.topic(topic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(consumer, throwable -> Log.e(TAG, String.format("Subscribe Error for topic %s", topic), throwable));
    }

    /**
     * Send a message to the server
     *
     * @param client      the stomp client
     * @param destination the destination to send the message to
     * @param message     the message to send
     */
    private static void sendMessage(StompClient client, String destination, String message) {
        client
                .send(destination, message)
                .doOnError(throwable -> Log.e(TAG, String.format("Error sending message to %s", destination), throwable))
                .subscribe();
    }

    /**
     * Connect to the websocket and subscribe to the lobby topic
     * Also subscribe to the lifecycle events, so we can log them
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
                            Log.e(TAG, "STOMP Connection failed", lifecycleEvent.getException());
                            break;
                        case OPENED:
                            Log.e(TAG, "STOMP Connection opened");
                            break;
                        case CLOSED:
                            Log.e(TAG, "STOMP Connection closed");
                            break;
                        default:
                            break;
                    }
                }));

        disposable.add(lifecycle);

        Disposable lobby =
                subscribeTo(client, "/topic/lobby", this::onLobbyChange);

        disposable.add(lobby);

        client.connect();
    }

    /**
     * Handle a message on the lobby topic
     *
     * @param stompMessage the message
     */
    private void onLobbyChange(StompMessage stompMessage) {
        Log.d(TAG, String.format("Received message: %s", stompMessage.getPayload()));
        currentLobby = gson.fromJson(stompMessage.getPayload(),
                new TypeToken<List<Player>>() {}.getType());
        notifyListeners(currentLobby);
    }

    /**
     * Join the lobby
     * @param player the player to join the lobby
     */
    public void joinLobby(Player player) {
        String data = gson.toJson(player);
        sendMessage(client, "/app/join", data);
    }

    /**
     * Leave the lobby
     * @param player the player to leave the lobby
     */
    public void leaveLobby(Player player) {
        String data = gson.toJson(player);
        sendMessage(client, "/app/leave", data);
    }

    /**
     * Get the current lobby
     * @return the current lobby
     */
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