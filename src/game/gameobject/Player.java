/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.myGame.MyKeyboard;
import game.myGame.MyMenu;
import game.myGame.MyPad;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Light;
import openGLEngine.Animation;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class Player extends Entity {

    private final Place place;
    public MyMenu menu;
    private final Animation anim;
    private boolean animate;
    private Controler ctrl;
    private Camera cam;

    public Player(int startX, int startY, int width, int height, int sx, int sy, String name, Place place, int x, int y, int playerNr) {
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
        initControler(playerNr);
    }

    private void initControler(int playerNr) {
        if (playerNr == 0) {
            ctrl = new MyKeyboard(this);
        } else if (playerNr == 1) {
            ctrl = new MyPad(this, 0);
        } else if (playerNr == 2) {
            ctrl = new MyPad(this, 1);
        } else if (playerNr == 3) {
            ctrl = new MyPad(this, 2);
        }
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

    public void renderName(Place place, Player player, Camera cam) {
        place.renderMessage(0, place.getXOff(cam) + getMidX(), place.getYOff(cam) + getBegOfY(), name, new Color(place.r, place.g, place.b));
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

    @Override
    protected void renderName(Place place, Camera cam) {
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
