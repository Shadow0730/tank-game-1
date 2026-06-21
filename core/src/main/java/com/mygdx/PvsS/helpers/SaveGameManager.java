package com.mygdx.PvsS.helpers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class SaveGameManager {
    private static final String SAVE_FILE = "savegame.json";
    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    /**
     * Save game data to JSON file
     */
    public static boolean saveGame(gamesavedata data) {
        try {
            String json = gson.toJson(data);
            FileHandle file = Gdx.files.local(SAVE_FILE);
            file.writeString(json, false);
            System.out.println("Game saved to: " + SAVE_FILE);
            return true;
        } catch (Exception e) {
            System.err.println("Error saving game: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Load game data from JSON file
     */
    public static gamesavedata loadGame() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (!file.exists()) {
                System.out.println("No save file found");
                return null;
            }

            String json = file.readString();
            gamesavedata data = gson.fromJson(json, gamesavedata.class);
            System.out.println("Game loaded from: " + SAVE_FILE);
            return data;
        } catch (Exception e) {
            System.err.println("Error loading game: " + e.getMessage());
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Check if save file exists
     */
    public static boolean hasSaveFile() {
        return Gdx.files.local(SAVE_FILE).exists();
    }

    /**
     * Delete save file
     */
    public static boolean deleteSaveFile() {
        try {
            FileHandle file = Gdx.files.local(SAVE_FILE);
            if (file.exists()) {
                file.delete();
                System.out.println("Save file deleted");
                return true;
            }
            return false;
        } catch (Exception e) {
            System.err.println("Error deleting save file: " + e.getMessage());
            return false;
        }
    }
}
