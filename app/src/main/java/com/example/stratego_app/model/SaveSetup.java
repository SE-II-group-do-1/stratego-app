package com.example.stratego_app.model;

import android.content.Context;
import android.util.JsonWriter;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class SaveSetup {

    private static String tag = "SaveSetup";
    private static Board gameBoard = ModelService.getInstance().getGameBoard();

    /**
     * serialize the board setup and save it to the storage
     * @param context
     */

    public static boolean saveGameSetup(Context context) {
        FileOutputStream fileOutStream = null;
        JsonWriter writer = null;
        try {
            fileOutStream = context.openFileOutput("game_setup.json", Context.MODE_PRIVATE);
            writer = new JsonWriter(new OutputStreamWriter(fileOutStream, "UTF-8"));
            writer.setIndent("  ");
            writer.beginObject();
            for (int y = 0; y < gameBoard.getBoard().length; y++) {
                for (int x = 0; x < gameBoard.getBoard()[y].length; x++) {
                    Piece piece = gameBoard.getField(y, x);
                    if (piece != null) {
                        writer.name(x + "," + y).value(piece.getRank().toString());
                    }
                }
            }
            writer.endObject();
            return true; // saved
        } catch (Exception e) {
            Log.e(tag, "Error saving game setup", e);
            return false;
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (fileOutStream != null) {
                    fileOutStream.close();
                }
            } catch (IOException e) {
                Log.e(tag, "Error closing streams", e);
            }
        }
    }

    public static Piece[][] readGameSetup(Context context){
        try (InputStream is = context.openFileInput("game_setup.json")) {
            Piece[][] savedSetup = new Piece[10][10];

            String jsonString = new Scanner(is, "UTF-8").useDelimiter("\\A").next();
            JSONTokener tokener = new JSONTokener(jsonString);
            JSONObject jsonObject = new JSONObject(tokener);

            Iterator<?> keys = jsonObject.keys();
            while (keys.hasNext()) {
                String key = keys.next().toString();
                String[] coordinates = key.split(",");
                int x = Integer.parseInt(coordinates[0]);
                int y = Integer.parseInt(coordinates[1]);
                Rank pieceRank = Rank.valueOf(jsonObject.get(key).toString());

                savedSetup[y][x] = new Piece(pieceRank, null);

            }
            Log.i("saveSetup", "done going through list");
            //check none of the pieces are null
            for(int y=6; y<10; y++){
                for(int x=0; x<10; x++){
                    if(savedSetup[y][x] == null) return null;
                }
            }
            return savedSetup;
        } catch (Exception e) {
            Log.e(tag, "Error reading game setup", e);
            return null;
        }
    }
}
