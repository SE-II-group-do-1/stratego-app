package com.example.stratego_app.connection;

public interface WebSocketMessageHandler<T> {

    void onMessageReceived(T message);


}
