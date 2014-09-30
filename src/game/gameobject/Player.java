/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.gameobject;

import game.gameobject.menu.MyMenu;
import game.place.Camera;
import game.place.Place;
import game.place.Light;
import openGLEngine.Animation;
import org.lwjgl.opengl.Display;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class Player extends Entity {

    protected final Place place;
    public MyMenu menu;
    protected boolean pressed_Light;
    protected final Animation anim;
    private boolean animate;
    private Controler ctrl;
    private Camera cam;

    public Player(int startX, int startY, int width, int height, int sx, int sy, String name, Place place, int playerNr) {
        this.name = name;
        this.width = width;
        this.height = height;
        this.sX = startX;
        this.sY = startY;
        this.place = place;
        this.top = false;
        this.speed = 8;
        this.emitter = true;
        init("apple", Display.getWidth() / 2 - width / 2 - sX, Display.getHeight() / 2 - width / 2 - sY, sx, sy);
        this.light = new Light("light", 1f, 1f, 1f, 3, 1024, 1024);
        this.anim = new Animation(2, spr, sx, sy, 500);
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
    protected boolean isColided(int magX, int magY
    ) {
        return (place.isPlCTl(magX, magY, this, cam) || place.isPlCObj(magX, magY, this));
    }

    @Override
    protected void move(int xPos, int yPos) {
        place.moveCam(xPos, yPos, cam);
    }

    public void renderName(Place place, Player player, Camera cam) {
        if (cam == player.getCam()) {
            if (player == this) {
                place.renderMessage(0, getMidX(), getBegOfY(), name, new Color(place.r, place.g, place.b));
            }
        } else {
            place.renderMessage(0, getMidX() - place.getXOff(player.getCam()) + place.getXOff(cam), getBegOfY() - place.getYOff(player.getCam()) + place.getYOff(cam), name, new Color(place.r, place.g, place.b));
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (spr != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            anim.render(animate);
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
}
