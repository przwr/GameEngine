/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.Drawer;
import engine.Methods;
import game.Settings;
import game.gameobject.GUIObject;
import game.place.Place;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author Wojtek
 */
public class GUIHandler extends GUIObject {

    private int mode, selected;
    private ArrayList<File> list;
    private String text = "";
    private final int tile;
    private final ObjectPlace objPlace;
    private final SimpleKeyboard key;
    private boolean firstLoop;

    private final int xStart, yStart;

    private final int DONOTHING = -1;
    private final int NAMING = 0;
    private final int CHOOSING = 1;
    private final int HELPING = 2;
    private final int QUESTIONING = 3;

    private final String[] help = new String[]{
        "H : Help",
        "M:                     Change mode",
        "S:                     Save as",
        "cltr + S:              Quicksave",
        "L:                     Load object",
        "",
        "TAB:                   Change background",
        "cltr + arrows :        Change selection",
        "cltr + Z :             Reset selection",
        "SPACE:                 Create",
        "DELETE:                Delete",
        "HOME:                  Set starting point",
        "",
        "//TILE MODE",
        "",
        "SHIFT + arrows :       Change tile",
        "T :                    Load spritesheet",
        "",
        "//BLOCK MODE",
        "",
        "SHIFT + arrows:        Change block height"};

    public GUIHandler(Place place) {
        super("guih", place);
        tile = place.getTileSize();
        objPlace = (ObjectPlace) place;
        mode = DONOTHING;
        key = new SimpleKeyboard();
        visible = false;
        xStart = (int) (tile * 0.1);
        yStart = (int) (tile * 2.5);
    }

    public void changeToNamingConsole() {
        mode = NAMING;
        visible = true;
        firstLoop = true;
    }

    public void changeToChooser(ArrayList<File> list) {
        mode = CHOOSING;
        this.list = list;
        selected = 0;
        visible = true;
    }

    public void changeToHelpingScreen() {
        mode = HELPING;
        visible = true;
    }

    public boolean isWorking() {
        return mode != DONOTHING;
    }

    private void stop() {
        mode = DONOTHING;
        visible = false;
    }

    private void renderNamingConsole() {
        key.keyboardStart();

        if (firstLoop) {
            while (Keyboard.next()) {
                Keyboard.getEventKey();
            }
            firstLoop = false;
        }
        text = Methods.editWithKeyboard(text);
        place.renderMessage(0, (int) (xStart * Settings.scale), (int) (yStart * Settings.scale),
                "Write filename: " + text, new Color(1f, 1f, 1f));

        if (key.keyPressed(Keyboard.KEY_RETURN)) {
            try (BufferedReader wczyt = new BufferedReader(new FileReader("res/objects/" + text + ".puz"))) {
                mode = QUESTIONING;
                wczyt.close();
                return;
            } catch (IOException e) {
            }
            objPlace.saveObject(text);
            stop();
        }

        key.keyboardEnd();
    }

    private void renderChoosingFile() {
        key.keyboardStart();

        place.renderMessage(0, (int) (xStart * Settings.scale), (int) (yStart * Settings.scale),
                ">", new Color(1f, 1f, 1f));

        int delta;
        for (int i = 0; i < list.size(); i++) {
            delta = (int) ((i - selected) * tile * 0.5);
            place.renderMessage(0, (int) ((xStart + tile * 0.2) * Settings.scale), (int) ((yStart + delta) * Settings.scale),
                    list.get(i).getName(), new Color(1f, 1f, 1f));
        }

        if (key.keyPressed(Keyboard.KEY_UP) && selected > 0) {
            selected--;
        }
        if (key.keyPressed(Keyboard.KEY_DOWN) && selected < list.size() - 1) {
            selected++;
        }
        if (key.keyPressed(Keyboard.KEY_RETURN)) {
            objPlace.getFile(list.get(selected));
            stop();
        }
        if (key.keyPressed(Keyboard.KEY_BACK)) {
            stop();
        }
        key.keyboardEnd();
    }

    private void renderHelp() {
        key.keyboardStart();

        place.renderMessage(0, (int) (xStart * Settings.scale), (int) (yStart * Settings.scale),
                ">", new Color(1f, 1f, 1f));

        int delta;
        for (int i = 0; i < help.length; i++) {
            delta = (int) ((i - selected) * tile * 0.5);
            place.renderMessage(0, (int) ((xStart + tile * 0.2) * Settings.scale), (int) ((yStart + delta) * Settings.scale),
                    help[i], new Color(1f, 1f, 1f));
        }

        if (key.keyPressed(Keyboard.KEY_UP) && selected > 0) {
            selected--;
        }
        if (key.keyPressed(Keyboard.KEY_DOWN) && selected < help.length - 1) {
            selected++;
        }
        if (key.keyPressed(Keyboard.KEY_RETURN) || key.keyPressed(Keyboard.KEY_BACK)) {
            stop();
        }
        key.keyboardEnd();
    }

    private void renderQuestion() {
        key.keyboardStart();
        place.renderMessage(0, (int) (xStart * Settings.scale), (int) (yStart * Settings.scale),
                "File with that name already exist.", new Color(1f, 1f, 1f));
        place.renderMessage(0, (int) (xStart * Settings.scale), (int) ((yStart + tile * 0.5) * Settings.scale),
                "Replace?", new Color(1f, 1f, 1f));
        place.renderMessage(0, (int) (xStart * Settings.scale), (int) ((yStart + tile) * Settings.scale),
                "YES[Enter] / NO[Backspace]", new Color(1f, 1f, 1f));

        if (key.keyPressed(Keyboard.KEY_RETURN)) {
            objPlace.saveObject(text);
            stop();
        }
        if (key.keyPressed(Keyboard.KEY_BACK)) {
            stop();
        }
        key.keyboardEnd();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (player != null) {
            glPushMatrix();
            if (Settings.scaled) {
                glScaled(Settings.scale, Settings.scale, 1);
            }
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(1 / Settings.scale, 1 / Settings.scale, 1);
            }
            switch (mode) {
                case QUESTIONING:
                    renderQuestion();
                    break;
                case NAMING:
                    renderNamingConsole();
                    break;
                case CHOOSING:
                    renderChoosingFile();
                    break;
                case HELPING:
                    renderHelp();
                    break;
            }
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

}
