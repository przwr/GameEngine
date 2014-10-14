/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.gameobject.inputs.*;
import game.myGame.MyController;
import game.myGame.MyMenu;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Light;
import Engine.Animation;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class Player extends Entity {

    private Place place;
    public MyMenu menu;
    private Animation anim;
    private boolean animate;
    public MyController ctrl;
    private Camera cam;

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

    public Player(boolean isFirst) {
        initControler(isFirst);
    }

    public void init(int startX, int startY, int width, int height, int sx, int sy, String name, Place place, int x, int y) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.sX = startX;
        this.sY = startY;
        this.place = place;
        this.top = false;
        this.setSpeed(8);
        this.emitter = true;
        init("apple", name, x, y, sx, sy);
        this.light = new Light("light", 1f, 1f, 1f, 1, 1024, 1024);
        this.anim = new Animation(2, spr, 500);
        animate = true;
    }

    private void initControler(boolean isFirst) {
        ctrl = new MyController(this);
        if (isFirst) {
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
        if ((getBegOfX() + magX) < 0 || (getEndOfX() + magX) > place.getWidth() || (getBegOfY() + magY) < 0 || (getEndOfY() + magY) > place.getHeight()) {
            return true;
        }
        return (getPlace().isObjCTl(magX, magY, this) || getPlace().isPlCObj(magX, magY, this));
    }

    @Override
    protected void move(int xPos, int yPos) {
        this.x = x + xPos;
        this.y = y + yPos;
        cam.move(xPos, yPos);
    }

    @Override
    public void renderName(Place place, Camera cam) {
        place.renderMessage(0, cam.getXOff() + getMidX(), cam.getYOff() + getBegOfY(), name, new Color(place.r, place.g, place.b));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (spr != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            getAnim().render(animate);
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
}
