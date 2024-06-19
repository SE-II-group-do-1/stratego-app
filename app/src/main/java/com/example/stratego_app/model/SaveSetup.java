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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.Iterator;
import java.util.Scanner;

public class SaveSetup {

    private static String tag = "SaveSetup";
    /**
     * serialize the board setup and save it to the storage
     */

    public static boolean saveGameSetup(Context context) {
        Gson gson = new GsonBuilder().create();
        String jsonString = gson.toJson(ModelService.getInstance().getGameBoard());

        FileOutputStream fos = null;
        OutputStreamWriter osw = null;
        try {
            fos = context.openFileOutput("game_setup.json", Context.MODE_PRIVATE);
            osw = new OutputStreamWriter(fos);
            osw.write(jsonString);
            osw.flush();
            return true;
        } catch (IOException e) {
        } finally {
            if (osw != null) {
                try {
                    osw.close();
                } catch (IOException e) {
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

    public static boolean readGameSetup(Context context){
        FileInputStream fis = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = context.openFileInput("game_setup.json");
            isr = new InputStreamReader(fis);
            br = new BufferedReader(isr);
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            Gson gson = new Gson();
            Board b = gson.fromJson(sb.toString(), Board.class);
            if(b != null && b.getField(9,9) != null){
                ModelService.getInstance().getGameBoard().setBoard(b);
                return true;
            }
            return false;
        } catch (IOException e) {
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                }
            }
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return false;
    }

}
