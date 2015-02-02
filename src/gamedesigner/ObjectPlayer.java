/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import game.gameobject.Player;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Light;
import engine.Drawer;
import engine.Methods;
import engine.Point;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Tile;
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
        initializeController();
    }

    private void initializeController() {
        controler = new ObjectController(this);
        controler.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
        controler.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
        controler.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
        controler.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        controler.initialize();
    }

    public void addUI(ObjectUI ui) {
        this.ui = ui;
    }

    @Override
    public void initialize(int startX, int startY, int width, int height, Place place, int x, int y) {
        scale = place.settings.SCALE;
        this.online = place.game.online;
        this.width = Methods.roundHalfUp(scale * width);
        this.height = Methods.roundHalfUp(scale * height);
        this.xStart = Methods.roundHalfUp(scale * startX);
        this.yStart = Methods.roundHalfUp(scale * startY);
        this.setWeight(2);
        this.emitter = true;
        initialize(name, Methods.roundHalfUp(scale * x), Methods.roundHalfUp(scale * y), place);
        this.sprite = place.getSpriteSheet("apple");
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, Methods.roundHalfUp(scale * 1024), Methods.roundHalfUp(scale * 1024), place); // 0.85f - 0.75f daje fajne cienie 1.0f usuwa cały cień
        this.animation = new Animation((SpriteSheet) sprite, 200);
        emits = false;
        setCollision(Rectangle.create(this.width, this.height / 2, OpticProperties.NO_SHADOW, this));
        tile = place.tileSize;
    }

    @Override
    public void initialize(int startX, int startY, int width, int height, Place place) {
        this.online = place.game.online;
        scale = place.settings.SCALE;
        this.width = Methods.roundHalfUp(scale * width);
        this.height = Methods.roundHalfUp(scale * height);
        this.xStart = Methods.roundHalfUp(scale * startX);
        this.yStart = Methods.roundHalfUp(scale * startY);
        this.setWeight(2);
        this.emitter = true;
        this.place = place;
        this.sprite = place.getSpriteSheet("apple");
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, Methods.roundHalfUp(scale * 1024), Methods.roundHalfUp(scale * 1024), place); // 0.85f - 0.75f daje fajne cienie 1.0f usuwa cały cień
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
        boolean cltr = key(KEY_LCONTROL);

        if (xtimer == 0) {
            ix = Methods.interval(0, ix + xPos, map.getTileWidth());
            setX(ix * tile);
            if (!cltr) {
                xStop = Methods.interval(0, xStop + xPos, map.getTileWidth());
            }
        }
        if (ytimer == 0) {
            iy = Methods.interval(0, iy + yPos, map.getTileHeight());
            setY(iy * tile);
            if (!cltr) {
                yStop = Methods.interval(0, yStop + yPos, map.getTileHeight());
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
        boolean pressed = false;
        int xPos = 0;
        int yPos = 0;

        if (key(KEY_LCONTROL) && key(KEY_Z)) {
            xStop = ix;
            yStop = iy;
        }

        if (key(KEY_UP)) {
            yPos--;
        } else if (key(KEY_DOWN)) {
            yPos++;
        } else {
            ytimer = 0;
        }
        if (key(KEY_LEFT)) {
            xPos--;
        } else if (key(KEY_RIGHT)) {
            xPos++;
        } else {
            xtimer = 0;
        }

        ui.setChange(key(KEY_T));

        if (xPos != 0 || yPos != 0) {
            if (ui.isChanged()) {
                if (xtimer == 0 && ytimer == 0) {
                    ui.changeCoordinates(xPos, yPos);
                    xtimer = 1;
                    ytimer = 1;
                }
            } else {
                move(xPos, yPos);
            }
        }

        if (key(KEY_SPACE) && !prevClick) {
            prevClick = true;
            pressed = true;
            int xStart = Math.min(ix, xStop);
            int yStart = Math.min(iy, yStop);
            int xEnd = Math.max(ix, xStop);
            int yEnd = Math.max(iy, yStop);
            for (int xTemp = xStart; xTemp <= xEnd; xTemp++) {
                for (int yTemp = yStart; yTemp <= yEnd; yTemp++) {
                    Point p = ui.getCoordinates();
                    ((ObjectMap)map).addTile(xTemp, yTemp, p.getX(), p.getX(), ui.getSpriteSheet());
                }
            }
        }

        if (key(KEY_DELETE) && !prevClick) {
            prevClick = true;
            pressed = true;
            int xStart = Math.min(ix, xStop);
            int yStart = Math.min(iy, yStop);
            int xEnd = Math.max(ix, xStop);
            int yEnd = Math.max(iy, yStop);
            for (int xTemp = xStart; xTemp <= xEnd; xTemp++) {
                for (int yTemp = yStart; yTemp <= yEnd; yTemp++) {
                    Tile newTile = map.getTile(xTemp, yTemp);
                    newTile.popTileFromStack();
                }
            }
        }
        
        if (!pressed)
            prevClick = false;
    }

    private boolean key(int k) {
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

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure figure, int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
    }
}
