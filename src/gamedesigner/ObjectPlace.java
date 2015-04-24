/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.FontBase;
import engine.Point;
import game.Game;
import engine.inout.IO;
import game.Settings;
import game.place.Place;
import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.Player;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Map;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.ArrayList;
import org.lwjgl.input.Keyboard;
import org.newdawn.slick.openal.SoundStore;

/**
 *
 * @author przemek
 */
public class ObjectPlace extends Place {

    private final Action changeSplitScreenMode;
    private final Action changeSplitScreenJoin;
    private final update[] updates = new update[2];

    private ObjectUI ui;
    private int mode;
    private String lastName;
    private GUIHandler guiHandler;
    private ObjectPlayer editor;

    private final SimpleKeyboard key;
    private boolean altMode, noBlocks, grid;
    private final String[] prettyOptions;
    private final boolean[] viewingOptions;
    private ObjectMap objmap;
    private UndoControl undo;

    public ObjectPlace(Game game, int tileSize) {
        super(game, tileSize);
        dayCycle.setTime(7, 0);
        lastName = "";
        changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END));

        prettyOptions = new String[]{"Tiles: ", "Background: ", "Blocks: ", "Block Outlines: ", "FGTiles: "};
        viewingOptions = new boolean[prettyOptions.length];
        for (int i = 0; i < viewingOptions.length; i++) {
            viewingOptions[i] = true;
        }
        key = new SimpleKeyboard();
    }

    @Override
    public void generateAsGuest() {
        objmap = new ObjectMap(mapIDcounter++, this, 10240, 10240, Place.tileSize);
        ui = new ObjectUI(Place.tileSize, sprites.getSpriteSheet("tlo"), this);
        guiHandler = new GUIHandler(this);
        maps.add(objmap);
        editor = ((ObjectPlayer) players[0]);
        editor.addGui(ui);
        editor.addGui(guiHandler);
        undo = new UndoControl(objmap, 20);
        //sounds.init("res");
        fonts = new FontBase(20);
        fonts.add("Amble-Regular", (int) (Settings.nativeScale * 24));
        standardFont = fonts.getFont(0);
        SoundStore.get().poll(0);
        initializeMethods();
    }

    @Override
    public void generateAsHost() {
        generateAsGuest();
    }

    @Override
    public void update() {
        updates[game.mode].update();
    }

    private void initializeMethods() {
        updates[0] = () -> {
            if (areKeysUsable()) {
                keyboardHandling();
            }
            if (playersCount > 1) {
                changeSplitScreenJoin.act();
                changeSplitScreenMode.act();
                if (changeSplitScreenJoin.isOn()) {
                    Settings.joinSplitScreen = !Settings.joinSplitScreen;
                }
                if (changeSplitScreenMode.isOn()) {
                    changeSSMode = true;
                }
                cameras[playersCount - 2].update();
            }
            updateAreas();
            for (int i = 0; i < playersCount; i++) {
                ((Player) players[i]).update();
            }
        };
        updates[1] = () -> {
            System.err.println("ONLINE?..... pfft....");
        };
    }

    private void updateAreas() {
        tempMaps.clear();
        for (int i = 0; i < playersCount; i++) {
            Map map = players[i].getMap();
            if (!tempMaps.contains(map)) {
                map.clearAreasToUpdate();
                tempMaps.add(map);
            }
            map.addAreasToUpdate(map.getNearAreas(players[i].getArea()));
        }
    }

    private void keyboardHandling() {
        key.keyboardStart();

        if (key.key(Keyboard.KEY_H)) {
            guiHandler.changeToHelpingScreen();
        }
        if (key.key(Keyboard.KEY_T)) {
            guiHandler.changeToChooser(IO.getSpecificFilesList("res/textures", "spr"));
        }
        if (key.key(Keyboard.KEY_L)) {
            guiHandler.changeToChooser(IO.getSpecificFilesList("res/objects", "puz"));
        }
        if (key.key(Keyboard.KEY_V)) {
            guiHandler.changeToViewingOptions(viewingOptions, prettyOptions);
        }
        if (key.key(Keyboard.KEY_G)) {
            grid = !grid;
        }
        if (key.keyPressed(Keyboard.KEY_U)) {
            undo.undo();
        }
        if (key.key(Keyboard.KEY_BACK) && key.keyPressed(Keyboard.KEY_LCONTROL)) {
            objmap.clear();
            lastName = "";
            printMessage("MAP CLEARED");
        }
        altMode = key.key(Keyboard.KEY_LMENU);

        if (key.keyPressed(Keyboard.KEY_1)) {
            if (mode == 0) {
                noBlocks = !noBlocks;
                printMessage("BLOCKS ARE " + (noBlocks ? "INVISIBLE" : "VISIBLE") + " NOW");
            } else {
                printMessage("TILE MODE");
            }
            setMode(0);
        }
        if (key.keyPressed(Keyboard.KEY_2)) {
            setMode(1);
            printMessage("BLOCK MODE");
        }
        if (key.keyPressed(Keyboard.KEY_3)) {
            setMode(2);
            printMessage("VIEWING MODE");
        }
        if (key.keyPressed(Keyboard.KEY_4)) {
            setMode(3);
            printMessage("OBJECT MODE");
        }

        if (key.keyPressed(Keyboard.KEY_S)) {
            if (key.key(Keyboard.KEY_LCONTROL) && !"".equals(lastName)) {
                saveObject(lastName);
            } else {
                guiHandler.changeToNamingConsole();
            }
        }
        key.keyboardEnd();
    }

    public void setViewingOption(int index) {
        switch (index) {
            case 0:
                objmap.setTilesVisibility(viewingOptions[index]);
                break;
            case 1:
                objmap.switchBackground();
                break;
            case 2:
                objmap.setBlocksVisibility(viewingOptions[index]);
                break;
            case 3:
                noBlocks = !viewingOptions[index];
                break;
            case 4:
                objmap.setFGTVisibility(viewingOptions[index]);
                break;
        }
    }

    public UndoControl getUndoControl() {
        return undo;
    }

    private void setMode(int mode) {
        this.mode = mode;
        editor.setMode(mode);
        ui.setMode(mode);
    }

    public boolean isAltMode() {
        return altMode;
    }

    public boolean isNoBlocksMode() {
        return noBlocks;
    }

    public boolean isGridEnabled() {
        return grid;
    }

    public void setCentralPoint(int x, int y) {
        objmap.setCentralPoint(x, y);
    }

    public int getMode() {
        return mode;
    }

    public boolean areKeysUsable() {
        return !guiHandler.isWorking();
    }

    public void saveObject(String name) {
        lastName = name;
        ArrayList<String> content = objmap.saveMap();

        try (PrintWriter save = new PrintWriter("res/objects/" + name + ".puz")) {
            content.stream().forEach((line) -> {
                save.println(line);
            });
            printMessage("Object \"" + name + ".puz\" was saved.");
            save.close();
        } catch (FileNotFoundException e) {
            printMessage("A file cannot be created!");
        }
    }

    @Override
    public int getPlayersCount() {
        if (game.mode == 0) {
            return playersCount;
        } else {
            return 1;
        }
    }

    public void clearMap() {
        objmap.clear();
        printMessage("Map cleaned");
    }

    public void getFile(File f) {
        String name = f.getName();
        String[] file = name.split("\\.");
        if (file[1].equals("spr")) {
            try {
                ui.setSpriteSheet(sprites.getSpriteSheet(file[0]));
                printMessage("Spritesheet \"" + name + "\" was loaded");
            } catch (java.lang.ClassCastException e) {
                printMessage("\"" + name + "\" is not a Spritesheet!");
            }
        } else {
            ObjectPO loaded = new ObjectPO(file[0], this);
            //PuzzleObject loaded = new PuzzleObject(file[0], this);
            Point p = loaded.getStartingPoint();
            objmap.clear();
            loaded.placePuzzle(p.getX(), p.getY(), objmap);
            printMessage("Object \"" + name + "\" was loaded");
            undo.removeMoves();
            lastName = file[0];
            editor.changeMap(objmap);
        }
    }

    private interface update {

        void update();
    }
}
