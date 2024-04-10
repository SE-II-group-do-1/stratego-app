package com.example.stratego_app.connection;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.gson.Gson;

import io.reactivex.Scheduler;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;
import okio.ByteString;
import ua.naiksoftware.stomp.Stomp;
import ua.naiksoftware.stomp.StompClient;
import ua.naiksoftware.stomp.dto.StompMessage;

public class WebsocketLobbyClient {
    private final String url = "ws://10.0.2.2:53216/ws/websocket";
    private final Gson gson = new Gson();
    private StompClient client;
    @SuppressLint("CheckResult")
    public void connect(){
        client = Stomp.over(Stomp.ConnectionProvider.OKHTTP, url);

        client.lifecycle()
        .subscribeOn(Schedulers.io())
        .observeOn(AndroidSchedulers.mainThread())
        .subscribe((lifecycleEvent -> {
            switch (lifecycleEvent.getType()){
                case ERROR:
                    Log.e("network", "error", lifecycleEvent.getException());
                    break;
                case OPENED:
                    Log.e("network", "opened");
                    break;
                case CLOSED:
                    Log.e("network", "closed");
                    break;
            }

        }));

        client.topic("/topic/lobby")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe((msg)->{
            Log.e("network", msg.getPayload());
        },e ->{
                    Log.e("network", "error", e);
                });
        client.connect();
    }
    public void joinLobby(Player player){
        String data = gson.toJson(player);
        client.send("/app/join", data);

    }
}