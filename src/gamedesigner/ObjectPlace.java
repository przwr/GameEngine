/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import gamedesigner.GUI.Help;
import game.Game;
import game.Settings;
import game.place.cameras.Camera;
import game.place.Place;
import engine.FontsHandler;
import engine.Point;
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
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class ObjectPlace extends Place {
    
    private final Action changeSplitScreenMode;
    private final Action changeSplitScreenJoin;
    private final Place place;
    private final update[] ups = new update[2];
    private final Help help;
    
    private File lastFile = new File(".");
    
    private ObjectUI ui;
    
    public ObjectPlace(Game game, int width, int height, int tileSize, Settings settnig, boolean isHost) {
        super(game, width, height, tileSize, settnig);
        this.help = new Help();
        place = this;
        changeSplitScreenMode = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_INSERT));
        changeSplitScreenJoin = new ActionOnOff(new InputKeyBoard(Keyboard.KEY_END));
        generate(isHost);
    }
    
    private void generate(boolean isHost) {
        ObjectMap polana = new ObjectMap(mapId++, this, width, height, tileSize);
        this.ui = new ObjectUI(tileSize, sprites.getSpriteSheet("tlo"), this);
        maps.add(polana);
        polana.addObj(ui);
        //sounds.init("res", settings);
        this.red = 0.75f;
        this.green = 0.75f;
        this.blue = 0.75f;
        fonts = new FontsHandler(20);
        fonts.add("Amble-Regular", (int) (settings.SCALE * 24));
        SoundStore.get().poll(0);
        initMethods();
    }
    
    @Override
    public void update() {
        ups[game.mode].up();
    }
    
    private void initMethods() {
        ups[0] = new update() {
            @Override
            public void up() {
                if (Keyboard.isKeyDown(Keyboard.KEY_I)) {
                    help.setVisible(true);
                }
                
                if (Keyboard.isKeyDown(Keyboard.KEY_L)) {
                    loadTextures();
                }
                
                if (playersLength > 1) {
                    changeSplitScreenJoin.act();
                    changeSplitScreenMode.act();
                    if (changeSplitScreenJoin.isOn()) {
                        settings.joinSS = !settings.joinSS;
                    }
                    if (changeSplitScreenMode.isOn()) {
                        changeSSMode = true;
                    }
                    cams[playersLength - 2].update();
                }
                for (int i = 0; i < playersLength; i++) {
                    ((Player) players[i]).update();
                }
                maps.stream().forEach((map) -> {
                    map.getSolidMobs().stream().forEach((mob) -> {
                        mob.update(game.place);
                    });
                });
            }
        };
        ups[1] = () -> {
            System.err.println("ONLINE?..... pfft....");
        };
    }
    
    @Override
    public int getPlayersLenght() {
        if (game.mode == 0) {
            return playersLength;
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
        
        void up();
    }
}
