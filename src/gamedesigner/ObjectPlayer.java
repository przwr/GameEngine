/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.OpticProperties;
import collision.Rectangle;
import game.gameobject.Player;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Light;
import engine.Drawer;
import engine.Methods;
import game.gameobject.inputs.InputKeyBoard;
import net.packets.Update;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import sprites.Animation;
import sprites.SpriteSheet;
import static org.lwjgl.input.Keyboard.*;

/**
 *
 * @author przemek
 */
public class ObjectPlayer extends Player {

    private int hs, vs, maxtimer;
    private int ix, iy;
    int xtimer, ytimer;
    private int tile;
    private int xStop, yStop;
    private boolean prevClick;

    private ObjectUI ui;
//    private final int[] tab = {GL_ZERO, GL_ONE, GL_SRC_COLOR, GL_ONE_MINUS_SRC_COLOR, GL_DST_COLOR,
//        GL_ONE_MINUS_DST_COLOR, GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA, GL_DST_ALPHA, GL_ONE_MINUS_DST_ALPHA,
//        GL_CONSTANT_COLOR, GL_ONE_MINUS_CONSTANT_COLOR, GL_CONSTANT_ALPHA, GL_ONE_MINUS_CONSTANT_ALPHA};

    public ObjectPlayer(boolean first, String name) {
        super(name);
        this.first = first;
        maxtimer = 7;
        xtimer = 0;
        ytimer = 0;
        initControler();
    }

    private void initControler() {
        ctrl = new ObjectController(this);
        ctrl.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
        ctrl.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
        ctrl.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
        ctrl.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        ctrl.init();
    }

    public void addUI(ObjectUI ui) {
        this.ui = ui;
    }

    @Override
    public void initialize(int startX, int startY, int width, int height, Place place, int x, int y) {
        scale = place.settings.SCALE;
        this.online = place.game.online;
        this.width = Methods.RoundHU(scale * width);
        this.height = Methods.RoundHU(scale * height);
        this.startX = Methods.RoundHU(scale * startX);
        this.startY = Methods.RoundHU(scale * startY);
        this.setWeight(2);
        this.emitter = true;
        init(name, Methods.RoundHU(scale * x), Methods.RoundHU(scale * y), place);
        this.sprite = place.getSpriteSheet("apple");
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, Methods.RoundHU(scale * 1024), Methods.RoundHU(scale * 1024), place); // 0.85f - 0.75f daje fajne cienie 1.0f usuwa cały cień
        this.animation = new Animation((SpriteSheet) sprite, 200);
        emits = false;
        setCollision(Rectangle.create(this.width, this.height / 2, OpticProperties.NO_SHADOW, this));
        tile = place.tileSize;
    }

    @Override
    public void initialize(int startX, int startY, int width, int height, Place place) {
        this.online = place.game.online;
        scale = place.settings.SCALE;
        this.width = Methods.RoundHU(scale * width);
        this.height = Methods.RoundHU(scale * height);
        this.startX = Methods.RoundHU(scale * startX);
        this.startY = Methods.RoundHU(scale * startY);
        this.setWeight(2);
        this.emitter = true;
        this.place = place;
        this.sprite = place.getSpriteSheet("apple");
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, Methods.RoundHU(scale * 1024), Methods.RoundHU(scale * 1024), place); // 0.85f - 0.75f daje fajne cienie 1.0f usuwa cały cień
        this.animation = new Animation((SpriteSheet) sprite, 200);
        emits = false;
        setCollision(Rectangle.create(this.width, this.height / 2, OpticProperties.NO_SHADOW, this));
        tile = place.tileSize;
    }

    @Override
    protected boolean isColided(int magX, int magY) {
        if (place != null) {
            return collision.isCollideSolid(getX() + magX, getY() + magY, map);
        }
        return false;
    }

    @Override
    protected void move(int xPos, int yPos) {
        boolean cltr = guzik(KEY_LCONTROL);

        if (xtimer == 0) {
            ix = Methods.Interval(0, ix + xPos, map.getTileWidth());
            setX(ix * tile);
            if (!cltr) {
                xStop = Methods.Interval(0, xStop + xPos, map.getTileWidth());
            }
        }
        if (ytimer == 0) {
            iy = Methods.Interval(0, iy + yPos, map.getTileHeight());
            setY(iy * tile);
            if (!cltr) {
                yStop = Methods.Interval(0, yStop + yPos, map.getTileHeight());
            }
        }
        if (cam != null) {
            cam.update();
        }
        xtimer++;
        ytimer++;
        if (xtimer >= maxtimer) {
            xtimer = 0;
        }
        if (ytimer >= maxtimer) {
            ytimer = 0;
        }
    }

    @Override
    protected void setPosition(int xPos, int yPos) {
        setX(xPos);
        setY(yPos);
        if (cam != null) {
            cam.update();
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            int d = 3;
            int xd = (Math.abs(ix - xStop) + 1) * tile;
            int yd = (Math.abs(iy - yStop) + 1) * tile;
            glTranslatef(Math.min(ix, xStop) * tile + xEffect, Math.min(iy, yStop) * tile + yEffect, 0);
            glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
            glColor4f(1f, 1f, 1f, 1f);
            Drawer.drawRectangle(-d, -d, xd + 2 * d, d);
            Drawer.drawRectangle(0, yd + d, xd + 2 * d, d);
            Drawer.drawRectangle(0, -yd, d, yd);
            Drawer.drawRectangle(xd + d, 0, d, yd);
            Drawer.refreshForRegularDrawing();
            glPopMatrix();
        }
    }

    @Override
    public void update() {
        boolean tileMove = false;
        int xPos = 0;
        int yPos = 0;

        if (guzik(KEY_LCONTROL) && guzik(KEY_Z)) {
            xStop = ix;
            yStop = iy;
        }

        if (guzik(KEY_UP)) {
            yPos--;
        } else if (guzik(KEY_DOWN)) {
            yPos++;
        } else {
            ytimer = 0;
        }
        if (guzik(KEY_LEFT)) {
            xPos--;
        } else if (guzik(KEY_RIGHT)) {
            xPos++;
        } else {
            xtimer = 0;
        }

        if (xPos != 0 || yPos != 0) {
            if (guzik(KEY_T)) {
                if (xtimer == 0 && ytimer == 0) {
                    ui.changeCoordinates(xPos, yPos);
                    xtimer = 1;
                    ytimer = 1;
                }
            } else {
                move(xPos, yPos);
            }
        }
    }

    private boolean guzik(int k) {
        return Keyboard.isKeyDown(k);
    }

    @Override
    public void sendUpdate(Place place) {
    }

    @Override
    public void updateOnline() {
    }

    @Override
    public void updateRest(Update up) {
    }

    @Override
    public void renderName(Place place, Camera cam) {
    }
}
