/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.myGame;

import collision.Rectangle;
import game.gameobject.inputs.*;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Light;
import engine.Animation;
import game.gameobject.Entity;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class MyPlayer extends Entity {

    public MyMenu menu;
    private Animation anim;
    private boolean animate;
    public MyController ctrl;
    private Camera cam;
    public boolean isFirst = false;

//    public Player(int startX, int startY, int width, int height, int sx, int sy, String name, Place place, int x, int y, boolean isFirst) {
//        this.name = name;
//        this.width = width;
//        this.height = height;
//        this.sX = startX;
//        this.sY = startY;
//        this.place = place;
//        this.top = false;
//        this.setSpeed(8);
//        this.emitter = true;
//        init("apple", name, x, y, sx, sy);
//        this.light = new Light("light", 1f, 1f, 1f, 1, 1024, 1024);
//        this.anim = new Animation(2, spr, 500);
//        animate = true;
//        initControler(isFirst);
//    }
    public MyPlayer(boolean isFirst, String name) {
        this.name = name;
        initControler(isFirst);
    }

    public void init(int startX, int startY, int width, int height, int sx, int sy, Place place, int x, int y, double SCALE) {
        this.width = (int) (SCALE * width);
        this.height = (int) (SCALE * height);
        this.sX = (int) (SCALE * startX);
        this.sY = (int) (SCALE * startY);
        this.top = false;
        this.setSpeed((int) (SCALE * 8));
        this.emitter = true;
        init("apple", name, (int) (SCALE * x), (int) (SCALE * y), (int) (SCALE * sx), (int) (SCALE * sy), place);
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, 1, (int) (SCALE * 1024), (int) (SCALE * 1024), place); // 0.85f - 0.75f daje fajne cienie 1.0f usuwa cały cień
        this.anim = new Animation(4, sprite, 200);
        animate = true;
        emits = false;
        setCollision(new Rectangle(sX, sY, this.width, this.height, this));
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

    public void addCamera(Camera cam) {
        this.cam = cam;
    }

    public void getInput() {
        ctrl.getInput();
    }

    public boolean isMenuOn() {
        return ctrl.isMenuOn();
    }

    public void getMenuInput() {
        ctrl.getMenuInput();
    }

    @Override
    protected boolean isColided(int magX, int magY) {
        if (place != null) {
            return collision.ifCollideSolid(getX() + magX, getY() + magY, place);
            /*if ((getBegOfX() + magX) < 0 || (getEndOfX() + magX) > place.getWidth() || (getBegOfY() + magY) < 0 || (getEndOfY() + magY) > place.getHeight()) {
             return true;
             }
             return (getPlace().isObjCTl(magX, magY, this) || getPlace().isPlCObj(magX, magY, this));*/
        }
        return false;
    }

    @Override
    protected void move(int xPos, int yPos) {
        x += xPos;
        y += yPos;
        cam.update();
    }

    @Override
    protected void setPosition(int xPos, int yPos) {
        x = xPos;
        y = yPos;
    }

    @Override
    public void renderName(Place place, Camera cam) {
        place.renderMessage(0, cam.getXOff() + getMidX(), cam.getYOff() + getBegOfY(), name, new Color(place.r, place.g, place.b));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            getAnim().render(animate);
            //glRectf(0f,0f, width, height);
            glPopMatrix();
        }
    }

    public void renderShadow(int xEffect, int yEffect, boolean isLit) {
        if (nLit != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            if (isLit) {
                lit.render();
            } else {
                nLit.render();
            }
            glPopMatrix();
        }
    }

    public void setAnimate(boolean animate) {
        this.animate = animate;
    }

    public Camera getCam() {
        return cam;
    }

    public void addMenu(MyMenu menu) {
        this.menu = menu;
    }

    public Animation getAnim() {
        return anim;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlaceToNull() {
        place = null;
    }
}
