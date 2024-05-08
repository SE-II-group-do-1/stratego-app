package com.example.stratego_app.model;

import android.content.Context;
import android.util.JsonWriter;
import android.util.Log;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class SaveSetup {

    private static String tag = "SaveSetup";
    private static Board gameBoard = ModelService.getInstance().getGameBoard();

    /**
     * serialize the board setup and save it to the storage at server
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
}
