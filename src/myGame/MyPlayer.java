/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import collision.Figure;
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
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class MyPlayer extends Player {

    private int hs, vs;

    public MyPlayer(boolean isFirst, String name) {
        super(name);
        this.isFirst = isFirst;
        initControler(isFirst);
    }

    @Override
    public void init(int startX, int startY, int width, int height, Place place, int x, int y) {
        double SCALE = place.settings.SCALE;
        this.width = Methods.RoundHU(SCALE * width);
        this.height = Methods.RoundHU(SCALE * height);
        this.sX = Methods.RoundHU(SCALE * startX);
        this.sY = Methods.RoundHU(SCALE * startY);
        this.setWeight(2);
        this.emitter = true;
        init(name, Methods.RoundHU(SCALE * x), Methods.RoundHU(SCALE * y), place);
        this.sprite = place.getSpriteSheet("apple");
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, Methods.RoundHU(SCALE * 1024), Methods.RoundHU(SCALE * 1024), place); // 0.85f - 0.75f daje fajne cienie 1.0f usuwa cały cień
        this.anim = new Animation((SpriteSheet) sprite, 200, this);
        animate = true;
        emits = false;
        scale = SCALE;
        place.addObj(this);
        setCollision(new Rectangle(this.width, this.height / 2, true, false, 0, this));
    }

    @Override
    public void init(int startX, int startY, int width, int height, Place place) {
        double SCALE = place.settings.SCALE;
        this.width = Methods.RoundHU(SCALE * width);
        this.height = Methods.RoundHU(SCALE * height);
        this.sX = Methods.RoundHU(SCALE * startX);
        this.sY = Methods.RoundHU(SCALE * startY);
        this.setWeight(2);
        this.emitter = true;
        this.place = place;
        this.sprite = place.getSpriteSheet("apple");
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, Methods.RoundHU(SCALE * 1024), Methods.RoundHU(SCALE * 1024), place); // 0.85f - 0.75f daje fajne cienie 1.0f usuwa cały cień
        this.anim = new Animation((SpriteSheet) sprite, 200, this);
        animate = true;
        emits = false;
        scale = SCALE;
        place.addObj(this);
        setCollision(new Rectangle(this.width, this.height / 2, true, false, 0, this));
    }

    private void initControler(boolean isFirst) {
        ctrl = new MyController(this);
        if (isFirst) {
            this.isFirst = true;
            ctrl.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
            ctrl.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
            ctrl.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
            ctrl.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        }
        ctrl.init();
    }

    @Override
    protected boolean isColided(int magX, int magY) {
        if (place != null) {
            return collision.ifCollideSolid(getX() + magX, getY() + magY, place);
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
        place.renderMessage(0, cam.getXOff() + getX(), (int) (cam.getYOff() + getY() - sprite.getSy() + 15 - jump),
                name, new Color(place.r, place.g, place.b));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);

            Drawer.setColor(new Color(0, 0, 0, 51));
            Drawer.drawElipse(0, 0, Methods.RoundHU((float) collision.getWidth() / 2), Methods.RoundHU((float) collision.getHeight() / 2), 15);
            Drawer.refreshColor(place);
            glTranslatef(0, (int) -jump, 0);

            getAnim().render(animate);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f) {
        // glTranslatef(getX() + xEffect, getY() + yEffect, 0);
        if (sprite != null) {
            glEnable(GL_TEXTURE_2D);
            glPushMatrix();
            glTranslatef((int) x + xEffect, (int) y + yEffect + (int) -jump, 0);
            if (isLit) {
                Drawer.drawShapeInColor(anim, color, color, color, 1);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            } else {
                Drawer.drawShapeInBlack(anim);
                glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
            }
            glPopMatrix();
            glDisable(GL_TEXTURE_2D);
        }
    }

    float a = 22.5f;  //TYLKO TYMCZASOWE!

    @Override
    public void update(Place place) {
        if (isJumping) {
            hop = false;
            jump = Math.abs(Methods.xRadius(a * 4, 70));
            a += Time.getDelta();
            if ((int) a == 68) {
                isJumping = false;
                a = 22.5f;
            }
        }
        hs = (int) (hspeed + myHspeed);
        vs = (int) (vspeed + myVspeed);
        canMove(hs, vs);
        brakeOthers();
    }

    @Override
    public void sendUpdate(Place place) {
        if (isJumping) {
            jump = Math.abs(Methods.xRadius(a * 4, 70));
            a += Time.getDelta();
            if ((int) a == 68) {
                isJumping = false;
                a = 22.5f;
            }
        }
        hs = (int) (hspeed + myHspeed);
        vs = (int) (vspeed + myVspeed);
        canMove(hs, vs);
        brakeOthers();
        if (place.game.online.server != null) {
            place.game.online.server.sendUpdate(getX(), getY(), isEmits(), isHop());
        } else {
            place.game.online.client.sendPlayerUpdate(id, getX(), getY(), isEmits(), isHop());
        }
    }
}
