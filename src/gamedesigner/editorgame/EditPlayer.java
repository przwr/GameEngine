/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner.editorgame;

import collision.OpticProperties;
import collision.Rectangle;
import engine.Animation;
import game.gameobject.Player;
import game.place.cameras.Camera;
import game.place.Place;
import engine.Drawer;
import engine.Methods;
import game.place.Light;
import net.packets.Update;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;
import sprites.SpriteSheet;

/**
 *
 * @author przemek
 */
public class EditPlayer extends Player {

    public boolean grid = true;
    private int hs, vs;

    public EditPlayer(boolean isFirst, String name) {
        super(name);
        this.first = isFirst;
    }

    @Override
    public void initialize(int startX, int startY, int width, int height, Place place, int x, int y) {
        this.scale = place.settings.SCALE;
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
        //place.addObj(this);
        setCollision(Rectangle.create(this.width, this.height / 2, OpticProperties.NO_SHADOW, this));
    }

    @Override
    public void initialize(int startX, int startY, int width, int height, Place place) {
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
        place.renderMessage(0, cam.getXOff() + getX(), (int) (cam.getYOff() + getY() - height + sprite.getSy() + collision.getHeight() / 2 - jump),
                name, new Color(place.red, place.green, place.blue));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() + yEffect, 0);

        Drawer.setColor(Color.white);
        Drawer.drawElipse(0, 0, collision.getWidth() / 2, collision.getHeight() / 2, 15);
        Drawer.refreshColor();
        glPopMatrix();
    }

    @Override
    public void update() {
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
    public void sendUpdate(Place place) {
    }

    @Override
    public void updateOnline() {
    }

    @Override
    public void updateRest(Update up) {
    }

//    public void initControler(boolean isFirst) {
//        ctrl = new MyController(this);
//        if (isFirst) {
//            this.isFirst = true;
//            ctrl.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
//            ctrl.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
//            ctrl.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
//            ctrl.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
//        }
//        ctrl.init();
//    }
}
