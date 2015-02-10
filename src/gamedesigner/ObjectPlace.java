/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.FontBase;
import engine.Methods;
import engine.Point;
import gamedesigner.GUI.Help;
import game.Game;
import game.Settings;
import game.place.cameras.Camera;
import game.place.Place;
import game.gameobject.Action;
import game.gameobject.ActionOnOff;
import game.gameobject.Player;
import game.gameobject.inputs.InputKeyBoard;
import gamedesigner.GUI.FileBox;
import gamedesigner.GUI.NamingScreen;
import gamedesigner.GUI.PathFinder;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.input.Keyboard.KEY_M;
import org.newdawn.slick.openal.SoundStore;

/**
 *
 * @author przemek
 */
public class ObjectPlace extends Place {

    private final Action changeSplitScreenMode;
    private final Action changeSplitScreenJoin;
    private final update[] updates = new update[2];
    private final Help help;

    private File lastFile = new File(".");

    private ObjectUI ui;
    private int mode;
    private Point centralPoint;

    private boolean pressed, prevClick;

    public ObjectPlace(Game game, int tileSize) {
        super(game, tileSize);
        this.centralPoint = new Point(0,0);
        this.help = new Help();
        changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END));
    }

    @Override
    public void generateAsGuest() {
        ObjectMap polana = new ObjectMap(mapID++, this, 10240, 10240, getTileSize());
        this.ui = new ObjectUI(getTileSize(), sprites.getSpriteSheet("tlo"), this);
        maps.add(polana);
        addGUI(ui);
        ((ObjectPlayer) players[0]).addUI(ui);
        //sounds.init("res");
        this.red = 0.75f;
        this.green = 0.75f;
        this.blue = 0.75f;
        fonts = new FontBase(20);
        fonts.add("Amble-Regular", (int) (Settings.scale * 24));
        SoundStore.get().poll(0);
        initializeMethods();
    }

    @Override
    public void generateAsHost() {
        ObjectMap polana = new ObjectMap(mapID++, this, 10240, 10240, getTileSize());
        this.ui = new ObjectUI(getTileSize(), sprites.getSpriteSheet("tlo"), this);
        maps.add(polana);
        addGUI(ui);
        ((ObjectPlayer) players[0]).addUI(ui);
        //sounds.init("res");
        this.red = 0.75f;
        this.green = 0.75f;
        this.blue = 0.75f;
        fonts = new FontBase(20);
        fonts.add("Amble-Regular", (int) (24));
        SoundStore.get().poll(0);
        initializeMethods();
    }

    @Override
    public void update() {
        updates[game.mode].update();
    }

    private void initializeMethods() {
        updates[0] = () -> {
            pressed = false;
            if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
                help.setVisible(true);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
                loadTextures();
            }
            if (keyPressed(KEY_M)) {
                mode++;
                switch (mode) {
                    case 1:
                        ui.setVisible(false);
                        break;
                    case 3:
                        ui.setVisible(true);
                        mode = 0;
                        break;
                }
            }

            if (keyPressed(Keyboard.KEY_S)) {
                new NamingScreen(this).setVisible(true);
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
            for (int i = 0; i < playersCount; i++) {
                ((Player) players[i]).update();
            }
            maps.stream().forEach((map) -> {
                map.getSolidMobs().stream().forEach((mob) -> {
                    mob.update();
                });
            });

            if (!pressed) {
                prevClick = false;
            }
        };
        updates[1] = () -> {
            System.err.println("ONLINE?..... pfft....");
        };
    }

    public void setCentralPoint(int x, int y) {
        centralPoint.set(x, y);
    }
    
    public Point getCentralPoint() {
        return centralPoint;
    }
    
    public int getMode() {
        return mode;
    }

    public boolean key(int k) {
        return Keyboard.isKeyDown(k);
    }

    public boolean keyPressed(int k) {
        if (key(k)) {
            pressed = true;
            if (!prevClick) {
                prevClick = true;
                return true;
            }
        }
        return false;
    }

    public boolean saveObject(String name, NamingScreen gui) {
        ArrayList<String> content = ((ObjectMap) maps.get(0)).saveMap();
        try (BufferedReader wczyt = new BufferedReader(new FileReader("res/objects/" + name + ".puz"))) {
            if (!gui.questMsg("File \"" + name + "\" already exists!\nReplace?")) {
                return false;
            }
            wczyt.close();
        } catch (IOException e) {
        }

        try (PrintWriter save = new PrintWriter("res/objects/" + name + ".puz")) {
            for (String line : content)
                save.println(line);
            gui.infoMsg("Object \"" + name + ".puz\" was saved.");
            save.close();
        } catch (FileNotFoundException e) {
            gui.errMsg("A file cannot be created!");
            return false;
        }
        return true;
    }
    
    @Override
    public int getPlayersCount() {
        if (game.mode == 0) {
            return playersCount;
        } else {
            return 1;
        }
    }

    public void loadTextures() {
        PathFinder pf = new PathFinder(this, lastFile, new FileNameExtensionFilter(
                "Textures (.spr)", "spr"), javax.swing.JFileChooser.FILES_ONLY);
        pf.setVisible(true);

        while (pf.isVisible()) {
            System.out.print("");
        }
    }

    public void getFile(FileBox f) {
        lastFile = f.getDirectory();
        String name = f.getSelectedFile().getName();
        String sp = name.split("\\.")[0];
        ui.setSpriteSheet(sprites.getSpriteSheet(sp));
    }

    @Override
    protected void renderText(Camera cam) {

    }

    private interface update {

        void update();
    }
}
