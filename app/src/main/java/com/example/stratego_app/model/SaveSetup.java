package com.example.stratego_app.model;

import android.content.Context;
import android.util.JsonWriter;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class SaveSetup {

    private static String tag = "SaveSetup";
    /**
     * serialize the board setup and save it to the storage
     */

    public static boolean saveGameSetup() {
        Gson gson = new GsonBuilder().create();
        String jsonString = gson.toJson(ModelService.getInstance().getGameBoard());

        try (FileWriter fileWriter = new FileWriter("game_setup.json")) {
            fileWriter.write(jsonString);
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public static boolean readGameSetup(){
        Gson gson = new Gson();

        try (FileReader fileReader = new FileReader("game_setup.json")) {
            Board b = gson.fromJson(fileReader, Board.class);
            if(b != null && b.getField(9,9) != null){
                ModelService.getInstance().getGameBoard().setBoard(b);
                return true;
            }
            return false;
        } catch (IOException e) {
            return false;
        }
    }

}
