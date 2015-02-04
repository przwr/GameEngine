/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.FontBase;
import engine.Methods;
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
import gamedesigner.GUI.PathFinder;
import java.io.File;
import javax.swing.filechooser.FileNameExtensionFilter;
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
    private final Help help;

    private File lastFile = new File(".");

    private ObjectUI ui;

    public ObjectPlace(Game game, int tileSize) {
        super(game, tileSize);
        this.help = new Help();
        changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END));
    }

    @Override
    public void generateAsGuest() {
        ObjectMap polana = new ObjectMap(mapId++, this, Methods.roundHalfUp(Settings.scale * 10240), Methods.roundHalfUp(Settings.scale * 10240), getTileSize());
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
        ObjectMap polana = new ObjectMap(mapId++, this, Methods.roundHalfUp(Settings.scale * 10240), Methods.roundHalfUp(Settings.scale * 10240), getTileSize());
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
    public void update() {
        updates[game.mode].update();
    }

    private void initializeMethods() {
        updates[0] = () -> {
            if (Keyboard.isKeyDown(Keyboard.KEY_H)) {
                help.setVisible(true);
            }

            if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
                loadTextures();
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
        };
        updates[1] = () -> {
            System.err.println("ONLINE?..... pfft....");
        };
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
