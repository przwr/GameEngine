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
import game.gameobject.GameObject;
import game.place.Place;
import game.place.cameras.Camera;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private final int tile, xStart, yStart;
    private final ObjectPlace objPlace;
    private final SimpleKeyboard key;
    private boolean firstLoop;

    private boolean[] options;
    private String[] prettyOptions;

    private final int DONOTHING = -1, NAMING = 0, CHOOSING = 1, HELPING = 2, QUESTIONING = 3, VIEWING = 4;

    private final Comparator<File> nameComparator = (File firstObject, File secondObject)
            -> firstObject.getName().compareTo(secondObject.getName());

    private final String[] help = new String[]{
        "H : Help",
        "1 ... 4:               Change mode",
        "S:                     Save as",
        "cltr + S:              Quicksave",
        "L:                     Load object",
        "cltr + backspace       Clear map",
        "",
        "cltr + arrows :        Change selection",
        "cltr + Z :             Reset selection",
        "A:                     Run mode",
        "BACKSPACE:             Cancel",
        "U                      Undo",
        "",
        "SPACE:                 Create",
        "DELETE:                Delete",
        "ALT:                   Create altered",
        "",
        "V:                     Visibility options",
        "B:                     Lock Block",
        "M:                     Move Blocks",
        "HOME:                  Set starting point",
        "",
        "//TILE MODE (1)",
        "",
        "SHIFT + arrows :       Change tile",
        "T :                    Load spritesheet",
        "",
        "//BLOCK MODE (2)",
        "",
        "SHIFT + arrows:        Change block height",
        "R                      Rounded blocks mode",
        "",
        "//OBJECT MODE (4)",
        "",
        "SHIFT + arrows:        Change link radius"};

    public GUIHandler(Place place) {
        super("guih", place);
        tile = Place.tileSize;
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
        Collections.sort(list, nameComparator);
        selected = 0;
        visible = true;
    }

    public void changeToHelpingScreen() {
        mode = HELPING;
        visible = true;
    }

    public void changeToViewingOptions(boolean[] options, String[] prettyOptions) {
        mode = VIEWING;
        this.options = options;
        selected = 0;
        this.prettyOptions = new String[options.length * 2];
        for (int i = 0; i < options.length; i++) {
            this.prettyOptions[2 * i] = prettyOptions[i];
            this.prettyOptions[2 * i + 1] = options[i] ? "ON" : "OFF";
        }
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
            return;
        }
        text = Methods.editWithKeyboard(text);
        Drawer.renderString("Write filename: " + text, (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()),
                place.standardFont, new Color(1f, 1f, 1f));

        if (key.keyPressed(Keyboard.KEY_RETURN)) {
            if (text.length() > 0) {
                try (BufferedReader wczyt = new BufferedReader(new FileReader("res/objects/" + text + ".puz"))) {
                    mode = QUESTIONING;
                    wczyt.close();
                    return;
                } catch (IOException e) {
                }
                objPlace.saveObject(text);
            }
            stop();
        }

        key.keyboardEnd();
    }

    private void renderChoosingFile() {
        key.keyboardStart();

        Drawer.renderString(">", (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()),
                place.standardFont, new Color(1f, 1f, 1f));

        int delta;
        for (int i = 0; i < list.size(); i++) {
            delta = (int) ((i - selected) * tile * 0.5);
            Drawer.renderString(list.get(i).getName(), (int) ((xStart + tile * 0.2) * Place.getCurrentScale()), (int) ((yStart + delta) * Place.getCurrentScale()),
                    place.standardFont, new Color(1f, 1f, 1f));
        }

        if (key.keyPressed(Keyboard.KEY_UP)) {
            selected--;
            if (selected < 0) {
                selected = list.size() - 1;
            }
        }
        if (key.keyPressed(Keyboard.KEY_DOWN)) {
            selected++;
            if (selected > list.size() - 1) {
                selected = 0;
            }
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

        Drawer.renderString(">", (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()),
                place.standardFont, new Color(1f, 1f, 1f));

        int delta;
        for (int i = 0; i < help.length; i++) {
            delta = (int) ((i - selected) * tile * 0.5);
            Drawer.renderString(help[i], (int) ((xStart + tile * 0.2) * Place.getCurrentScale()), (int) ((yStart + delta) * Place.getCurrentScale()),
                    place.standardFont, new Color(1f, 1f, 1f));
        }
        if (key.keyPressed(Keyboard.KEY_UP)) {
            selected--;
            if (selected < 0) {
                selected = help.length - 1;
            }
        }
        if (key.keyPressed(Keyboard.KEY_DOWN)) {
            selected++;
            if (selected > help.length - 1) {
                selected = 0;
            }
        }
        if (key.keyPressed(Keyboard.KEY_RETURN) || key.keyPressed(Keyboard.KEY_BACK)) {
            stop();
        }
        key.keyboardEnd();
    }

    private void renderViewingOptions() {
        key.keyboardStart();

        Drawer.renderString(">", (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()),
                place.standardFont, new Color(1f, 1f, 1f));

        int delta;
        for (int i = 0; i < options.length; i++) {
            delta = (int) ((i - selected) * tile * 0.5);
            Drawer.renderString(prettyOptions[2 * i] + prettyOptions[2 * i + 1], (int) ((xStart + tile * 0.2) * Place.getCurrentScale()), (int) ((yStart + delta) * Place.getCurrentScale()),
                    place.standardFont, new Color(1f, 1f, 1f));
        }

        if (key.keyPressed(Keyboard.KEY_UP)) {
            selected--;
            if (selected < 0) {
                selected = options.length - 1;
            }
        }
        if (key.keyPressed(Keyboard.KEY_DOWN)) {
            selected++;
            if (selected > options.length - 1) {
                selected = 0;
            }
        }
        if (key.keyPressed(Keyboard.KEY_RETURN)) {
            options[selected] = !options[selected];
            prettyOptions[2 * selected + 1] = options[selected] ? "ON" : "OFF";
            objPlace.setViewingOption(selected);
        }
        if (key.keyPressed(Keyboard.KEY_BACK)) {
            stop();
        }
        key.keyboardEnd();
    }

    private void renderQuestion() {
        key.keyboardStart();
        Drawer.renderString("File with that name already exist.", (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()),
                place.standardFont, new Color(1f, 1f, 1f));
        Drawer.renderString("Replace?", (int) (xStart * Place.getCurrentScale()), (int) ((yStart + tile * 0.5) * Place.getCurrentScale()),
                place.standardFont, new Color(1f, 1f, 1f));
        Drawer.renderString("YES[Enter] / NO[Backspace]", (int) (xStart * Place.getCurrentScale()), (int) ((yStart + tile) * Place.getCurrentScale()),
                place.standardFont, new Color(1f, 1f, 1f));

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
                glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
            }
            glTranslatef(xEffect, yEffect, 0);
            if (Settings.scaled) {
                glScaled(1 / Place.getCurrentScale(), 1 / Place.getCurrentScale(), 1);
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
                case VIEWING:
                    renderViewingOptions();
                    break;
            }
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

}
