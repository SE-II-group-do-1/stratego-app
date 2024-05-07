package com.example.stratego_app.connection;

import com.example.stratego_app.connection.LobbyClient;
import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Piece;
import com.example.stratego_app.model.Player;

import java.util.HashMap;
import java.util.Map;

public class ToMap {
    public static Map<String, Object> updateToObject(int y, int x, Piece piece, Player initiator){
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("y", y);
        toReturn.put("x", x);
        toReturn.put("piece", piece);
        toReturn.put("initiator", initiator);
        return toReturn;
    }

    public static Map<String, Object> leaveToObject(Player player){
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("id", LobbyClient.getInstance().getCurrentLobby());
        toReturn.put("player", player);
        return toReturn;
    }

    public static Map<String, Object> setupToObject(Player player, Board board){
        Map<String, Object> toReturn = new HashMap<>();
        toReturn.put("board", board);
        toReturn.put("player", player);
        return toReturn;
    }
}
