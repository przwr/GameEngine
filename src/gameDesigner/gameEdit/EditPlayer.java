/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gameDesigner.gameEdit;

import myGame.*;
import collision.Rectangle;
import game.gameobject.Player;
import game.place.cameras.Camera;
import game.place.Place;
import engine.Drawer;
import engine.Methods;
import game.gameobject.inputs.InputKeyBoard;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class EditPlayer extends Player {

    public boolean grid = true;
    private int hs, vs;

    public EditPlayer(boolean isFirst, String name) {
        super(name);
        this.isFirst = isFirst;
    }

    @Override
    public void init(int startX, int startY, int width, int height, int sw, int sh, Place place, int x, int y) {
        double SCALE = place.settings.SCALE;
        this.width = (int) (SCALE * width);
        this.height = (int) (SCALE * height);
        this.sX = (int) (SCALE * startX);
        this.sY = (int) (SCALE * startY);
        this.setWeight(1);
        this.emitter = true;
        init(name, Methods.RoundHU((int) (SCALE * x)), Methods.RoundHU((int) (SCALE * y)), place);
        emits = false;
        scale = SCALE;
        place.addObj(this);
        setCollision(new Rectangle(0, 0, cam.getDwidth() * 2, cam.getDwidth() * 2, true, false, this));
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
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect, 0);

        Drawer.setColor(Color.white);
        Drawer.drawElipse(0, 0, collision.getWidth() / 2, collision.getHeight() / 2, 15);
        Drawer.refreshColor(place);
        glPopMatrix();
    }

    @Override
    public void update(Place place) {
        int mspeed = 10;
        if (Keyboard.isKeyDown(Keyboard.KEY_Z)) {
            mspeed *= 2;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_UP)) {
            vs -= mspeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_DOWN)) {
            vs += mspeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_LEFT)) {
            hs -= mspeed;
        }
        if (Keyboard.isKeyDown(Keyboard.KEY_RIGHT)) {
            hs += mspeed;
        }
        canMove(hs, vs);
        brakeOthers();
    }

    @Override
    public void init(int startX, int startY, int width, int height, int sw, int sh, Place place) {
    }

    @Override
    public void sendUpdate(Place place) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color) {
    }

    @Override
    public void initControler(boolean isFirst) {
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
}
