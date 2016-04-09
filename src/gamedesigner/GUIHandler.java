/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.systemcommunication.IO;
import engine.utilities.Methods;
import engine.utilities.SimpleKeyboard;
import game.gameobject.GUIObject;
import game.place.Place;
import game.text.fonts.TextMaster;
import game.text.fonts.TextPiece;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * @author Wojtek
 */
public class GUIHandler extends GUIObject {

    public static TextPiece text;
    private final int tile, xStart, yStart;
    private final ObjectPlace objPlace;
    private final SimpleKeyboard key;
    private final int DO_NOTHING = -1, NAMING = 0, CHOOSING = 1, HELPING = 2, QUESTIONING = 3, VIEWING = 4;
    private final Comparator<File> nameComparator = (File o1, File o2) -> {
        if ((o1.isDirectory() && o2.isDirectory()) || (!o1.isDirectory() && !o2.isDirectory())) {
            return o1.getName().compareTo(o2.getName());
        } else if (o1.isDirectory()) {
            return -1;
        } else {
            return 1;
        }
    };
    private final String[] help = new String[]{
            "H:", "Help",
            "1 ... 4:", "Change mode",
            "S:", "Save as",
            "CTRL + S:", "QuickSave",
            "L:", "Load object",
            "CTRL + BACKSPACE:", "Clear map",
            "",
            "CTRL + ARROWS:", "Change selection",
            "CTRL + Z:", "Reset selection",
            "A:", "Run mode",
            "BACKSPACE:", "Cancel",
            "U:", "Undo",
            "",
            "SPACE:", "Create",
            "DELETE:", "Delete",
            "ALT:", "Create altered",
            "",
            "+:", "Zoom in/out",
            "V:", "Visibility options",
            "B:", "Lock Block",
            "M:", "Move Blocks",
            "HOME:", "Set starting point",
            "PAGE UP/DOWN:", "Raise/Lower elevation",
            "",
            "//TILE MODE (1)",
            "",
            "SHIFT + ARROWS:", "Change tile",
            "T:", "Load spriteSheet",
            "Q:", "Switch to light-based mode",
            "",
            "//BLOCK MODE (2)",
            "",
            "SHIFT + ARROWS:", "Change block height",
            "R:", "Rounded blocks mode",
            "C:", "Place rounded block with last settings",
            "",
            "//OBJECT MODE (4)",
            "",
            "SHIFT + ARROWS:", "Change link radius"};
    private int mode, selected;
    private ArrayList<File> list;
    private String string = "", extension;
    private boolean firstLoop;
    private boolean[] options;
    private String[] prettyOptions;
    private File previous;

    private int helpLength;

    public GUIHandler(Place place) {
        super("gui", place);
        tile = Place.tileSize;
        objPlace = (ObjectPlace) place;
        mode = DO_NOTHING;
        key = new SimpleKeyboard();
        visible = false;
        xStart = (int) (tile * 0.1);
        yStart = (int) (tile * 2.5);
        if (text == null) {
            text = new TextPiece("", 24, TextMaster.getFont("Lato-Regular"), Display.getWidth(), false);
        }
        for (String s : help) {
            if (!s.equals("") && s.charAt(s.length() - 1) == ':') {
                helpLength = Math.max(helpLength, text.getTextWidth(s, text.getFontSize()));
            }
        }
        helpLength *= 1.1;
    }

    public void changeToNamingConsole() {
        mode = NAMING;
        visible = true;
        firstLoop = true;
    }

    public void changeToChooser(ArrayList<File> list, String extension) {
        mode = CHOOSING;
        this.list = list;
        this.extension = extension;
        previous = list.get(0).getParentFile();
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
            this.prettyOptions[2 * i] = prettyOptions[i] + ": ";
            this.prettyOptions[2 * i + 1] = options[i] ? "ON" : "OFF";
        }
        visible = true;
    }

    public boolean isWorking() {
        return mode != DO_NOTHING;
    }

    private void stop() {
        mode = DO_NOTHING;
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
        string = Methods.editWithKeyboard(string);
        text.setText("Write filename: " + string);
        TextMaster.renderOnce(text, (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()));

        if (key.keyPressed(Keyboard.KEY_RETURN)) {
            if (string.length() > 0) {
                try {
                    FileReader fl = new FileReader("res/objects/" + string + ".puz");
                    BufferedReader load = new BufferedReader(fl);
                    mode = QUESTIONING;
                    load.close();
                    fl.close();
                    return;
                } catch (IOException e) {
                }
                objPlace.saveObject(string);
            }
            stop();
        }

        key.keyboardEnd();
    }

    private void renderChoosingFile() {
        key.keyboardStart();

        TextMaster.startRenderText();
        text.setText(">");
        TextMaster.render(text, (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()));

        int delta;
        String name;
        File tmp;
        for (int i = 0; i < list.size(); i++) {
            tmp = list.get(i);
            delta = (int) ((i - selected) * tile * 0.5);
            if (tmp == previous) {
                name = "../";
            } else if (tmp.isDirectory()) {
                name = "<" + tmp.getName() + ">";
            } else {
                name = tmp.getName();
            }
            text.setText(name);
            TextMaster.render(text, (int) ((xStart + tile * 0.2) * Place.getCurrentScale()), (int) ((yStart + delta) * Place.getCurrentScale()));
        }
        TextMaster.endRenderText();

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
            if (list.get(selected).isDirectory()) {
                previous = list.get(selected).getParentFile();
                list = IO.getSpecificFilesList(list.get(selected), extension);
                if (!previous.getName().equals("res")) {
                    list.add(previous);
                }
                Collections.sort(list, nameComparator);
                selected = 0;
            } else {
                objPlace.getFile(list.get(selected));
                stop();
            }
        }
        if (key.keyPressed(Keyboard.KEY_BACK)) {
            stop();
        }
        key.keyboardEnd();
    }

    private void renderHelp() {
        key.keyboardStart();

        TextMaster.startRenderText();
        text.setText(">");
        TextMaster.render(text, (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()));
        int delta;
        int index = 0;
        for (int i = 0; i < help.length; i++, index++) {
            delta = (int) ((index - selected) * tile * 0.5);
            text.setText(help[i]);
            TextMaster.render(text, (int) ((xStart + tile * 0.4) * Place.getCurrentScale()), (int) ((yStart + delta) * Place.getCurrentScale()));
            if (!help[i].equals("") && help[i].charAt(help[i].length() - 1) == ':') {
                text.setText(help[++i]);
                TextMaster.render(text, helpLength + (int) ((xStart + tile * 0.2) * Place.getCurrentScale()),
                        (int) ((yStart + delta) * Place.getCurrentScale()));
            }
        }
        TextMaster.endRenderText();

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

        TextMaster.startRenderText();
        text.setText(">");
        TextMaster.render(text, (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()));
        int delta;
        for (int i = 0; i < options.length; i++) {
            delta = (int) ((i - selected) * tile * 0.5);
            text.setText(prettyOptions[2 * i] + prettyOptions[2 * i + 1]);
            TextMaster.render(text, (int) ((xStart + tile * 0.2) * Place.getCurrentScale()), (int) ((yStart + delta) * Place.getCurrentScale()));
        }
        TextMaster.endRenderText();

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
        TextMaster.startRenderText();
        text.setText("File with that name already exist.");
        TextMaster.render(text, (int) (xStart * Place.getCurrentScale()), (int) (yStart * Place.getCurrentScale()));
        text.setText("Replace?");
        TextMaster.render(text, (int) (xStart * Place.getCurrentScale()), (int) ((yStart + tile * 0.5) * Place.getCurrentScale()));
        text.setText("YES[Enter] / NO[Backspace]");
        TextMaster.render(text, (int) (xStart * Place.getCurrentScale()), (int) ((yStart + tile) * Place.getCurrentScale()));
        TextMaster.endRenderText();
        if (key.keyPressed(Keyboard.KEY_RETURN)) {
            objPlace.saveObject(string);
            stop();
        }
        if (key.keyPressed(Keyboard.KEY_BACK)) {
            stop();
        }
        key.keyboardEnd();
    }

    @Override
    public void render() {
        if (player != null) {
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
        }
    }

}
