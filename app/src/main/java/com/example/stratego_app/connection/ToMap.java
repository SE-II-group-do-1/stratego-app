package com.example.stratego_app.connection;

import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Player;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;


import java.util.HashMap;
import java.util.Map;

import ua.naiksoftware.stomp.dto.StompMessage;

public class ToMap {

    private static final Gson gson = new Gson();

    public static Map<String, Object> updateToObject(Board b, int initiator, int lobby){
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("board", b);
        toReturn.put("initiator", initiator);
        toReturn.put("lobby", lobby);
        return toReturn;
    }

    public static <type> type parseMessage(StompMessage message){
        return gson.<type>fromJson(message.getPayload(),
                new TypeToken<type>() {
                }.getType());
    }
}
