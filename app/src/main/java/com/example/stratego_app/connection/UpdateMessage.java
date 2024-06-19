package com.example.stratego_app.connection;


import com.example.stratego_app.model.Board;
import com.example.stratego_app.model.Color;
import com.example.stratego_app.model.Player;

public class UpdateMessage {
    public Board getBoard() {
        return board;
    }

    public void setBoard(Board board) {
        this.board = board;
    }

    public int getInitiator() {
        return initiator;
    }

    public void setInitiator(int initiator) {
        this.initiator = initiator;
    }

    public int getLobbyID() {
        return lobbyID;
    }

    public void setLobbyID(int lobbyID) {
        this.lobbyID = lobbyID;
    }
    public void setWinner(Color winner){
        this.winner = winner;
    }
    public Color getWinner() {
        return winner;
    }
    public void setClose(boolean close){
        this.close = close;
    }
    public boolean getClose(){
        return this.close;
    }

    public void setCheat(boolean cheat){
        this.cheat = cheat;
    }
    public boolean getCheat(){
        return this.cheat;
    }
    public void setCheck(boolean check){
        this.check = check;
    }
    public boolean getCheck(){
        return this.check;
    }

    private Board board;
    private int initiator;
    private int lobbyID;
    private Color winner;
    private boolean close;
    private boolean cheat;
    private boolean check;

}

