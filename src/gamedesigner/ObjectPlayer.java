/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import gamecontent.*;
import collision.Figure;
import collision.OpticProperties;
import collision.Rectangle;
import game.gameobject.Player;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Light;
import engine.Animation;
import engine.Drawer;
import engine.Methods;
import engine.Time;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Map;
import game.place.WarpPoint;
import net.jodk.lang.FastMath;
import net.packets.MPlayerUpdate;
import net.packets.Update;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class ObjectPlayer extends Player {

    private int hs, vs;

    public ObjectPlayer(boolean first, String name) {
        super(name);
        this.first = first;
        if (first) {
            initControlerForFirst();
        } else {
            initControler();
        }
    }

    private void initControlerForFirst() {
        ctrl = new MyController(this);
        ctrl.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
        ctrl.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
        ctrl.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
        ctrl.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        ctrl.inputs[4] = new InputKeyBoard(Keyboard.KEY_UP);
        ctrl.inputs[5] = new InputKeyBoard(Keyboard.KEY_DOWN);
        ctrl.inputs[6] = new InputKeyBoard(Keyboard.KEY_LEFT);
        ctrl.inputs[7] = new InputKeyBoard(Keyboard.KEY_RIGHT);
        ctrl.init();
    }

    private void initControler() {
        ctrl = new MyController(this);
        ctrl.init();
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
        this.anim = new Animation((SpriteSheet) sprite, 200, this);
        animate = true;
        emits = false;
        setCollision(Rectangle.create(this.width, this.height / 2, OpticProperties.NO_SHADOW, this));
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
        this.anim = new Animation((SpriteSheet) sprite, 200, this);
        animate = true;
        emits = false;
        setCollision(Rectangle.create(this.width, this.height / 2, OpticProperties.NO_SHADOW, this));
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
        setX(x + xPos);
        setY(y + yPos);
        if (cam != null) {
            cam.update();
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
    public void renderName(Place place, Camera cam) {
        place.renderMessage(0, cam.getXOff() + getX(), (int) (cam.getYOff() + getY() + sprite.getSy() + collision.getHeight() / 2 - jump),
                name, new Color(place.red, place.green, place.blue));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            Drawer.setColor(new Color(0, 0, 0, 51));
            Drawer.drawElipse(0, 0, Methods.RoundHU((float) collision.getWidth() / 2), Methods.RoundHU((float) collision.getHeight() / 2), 15);
            Drawer.refreshColor();
            glTranslatef(0, (int) -jump, 0);
            getAnim().render(animate);
            glPopMatrix();
        }
    }

    float a = 22.6f;  //TYLKO TYMCZASOWE!

    @Override
    public void update() {
        if (jumping) {
            hop = false;
            jump = FastMath.abs(Methods.xRadius(a * 4, 70));
            a += Time.getDelta();
            if ((int) a == 68) {
                jumping = false;
                a = 22.6f;
            }
        }
        hs = (int) (hspeed + myHspeed);
        vs = (int) (vspeed + myVspeed);
        canMove(hs, vs);
        for (WarpPoint w : map.getWarps()) {
            if (w.getCollision() != null) {
                if (w.getCollision().isCollideSingle(w.getX(), w.getY(), collision)) {
                    w.Warp(this);
                }
            }
        }
        brakeOthers();
    }

    @Override
    public synchronized void sendUpdate(Place place) {
        if (jumping) {
            jump = FastMath.abs(Methods.xRadius(a * 4, 70));
            a += Time.getDelta();
            if ((int) a == 68) {
                jumping = false;
                a = 22.5f;
            }
        }
        hs = (int) (hspeed + myHspeed);
        vs = (int) (vspeed + myVspeed);
        canMove(hs, vs);
        for (WarpPoint warp : map.getWarps()) {
            if (warp.getCollision() != null) {
                if (warp.getCollision().isCollideSingle(warp.getX(), warp.getY(), collision)) {
                    warp.Warp(this);
                }
            }
        }
        brakeOthers();
        if (online.server != null) {
            online.server.sendUpdate(map.getId(), getX(), getY(), isEmits(), isHop());
        } else if (online.client != null) {
            online.client.sendPlayerUpdate(map.getId(), id, getX(), getY(), isEmits(), isHop());
            online.past[online.pastNr++].set(getX(), getY());
            if (online.pastNr >= online.past.length) {
                online.pastNr = 0;
            }
        } else {
            online.g.endGame();
        }
    }

    @Override
    public synchronized void updateRest(Update up) {
        try {
            Map map = place.getMapById(((MPlayerUpdate) up).getMapId());
            if (map != null) {
                changeMap(map);
            }
            if (((MPlayerUpdate) up).isHop()) {
                setIsJumping(true);
            }
            setEmits(((MPlayerUpdate) up).isEmits());
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public synchronized void updateOnline() {
        try {
            if (jumping) {
                jump = FastMath.abs(Methods.xRadius(a * 4, 70));
                a += Time.getDelta();
                if ((int) a == 68) {
                    jumping = false;
                    a = 22.5f;
                }
            }
        } catch (Exception e) {
            System.out.println("ERROR: " + e.getMessage());
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect + (int) -jump, 0);
            Drawer.drawShapeInShade(anim, color);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect + (int) -jump, 0);
            Drawer.drawShapeInBlack(anim);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, float color, Figure f, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect + (int) -jump, 0);
            Drawer.drawShapeInShade(anim, color, xStart, xEnd);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure f, int xStart, int xEnd) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect + (int) -jump, 0);
            Drawer.drawShapeInBlack(anim, xStart, xEnd);
            glPopMatrix();
        }
    }
}
