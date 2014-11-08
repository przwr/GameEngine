/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import collision.Rectangle;
import game.gameobject.inputs.*;
import game.gameobject.Player;
import game.place.cameras.Camera;
import game.place.Place;
import game.place.Light;
import engine.Animation;
import engine.Drawer;
import engine.Methods;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import org.newdawn.slick.Color;

/**
 *
 * @author przemek
 */
public class MyPlayer extends Player {

    public MyPlayer(boolean isFirst, String name) {
        super(name);
        this.isFirst = isFirst;
        initControler(isFirst);
    }

    @Override
    public void init(int startX, int startY, int width, int height, int sw, int sh, Place place, int x, int y, double SCALE) {
        this.width = (int) (SCALE * width);
        this.height = (int) (SCALE * height);
        this.sX = (int) (SCALE * startX);
        this.sY = (int) (SCALE * startY);
        this.top = false;
        this.setWeight(1);
        this.emitter = true;
        init("apple", name, (int) (SCALE * x), (int) (SCALE * y), (int) (SCALE * sw), (int) (SCALE * sh), place);
        this.light = new Light("light", 0.85f, 0.85f, 0.85f, (int) (SCALE * 1024), (int) (SCALE * 1024), place); // 0.85f - 0.75f daje fajne cienie 1.0f usuwa cały cień
        this.anim = new Animation(4, sprite, 200);
        animate = true;
        emits = false;
        scale = SCALE;
        place.addObj(this);
        setCollision(new Rectangle(this.width, this.height / 2, true, false, this));
    }

    @Override
    public final void initControler(boolean isFirst) {
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
        place.renderMessage(0, cam.getXOff() + getX(), (int) (cam.getYOff() + getY() - sprite.getSy() + 15 - jump),
                name, new Color(place.r, place.g, place.b));
    }

    @Override
    public void render(int xEffect, int yEffect) {
        if (sprite != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);

            Drawer.setColor(new Color(0, 0, 0, 51));
            Drawer.drawElipse(0, 0, collision.getWidth() / 2, collision.getHeight() / 2, 15);
            Drawer.refreshColor(place);

            glTranslatef(0, (int) -jump, 0);
            //Drawer.setColor(Color.white);
            //glBlendFunc(GL_DST_ALPHA, GL_DST_ALPHA);
            //glDisable(GL_TEXTURE_2D);
            //glBlendColor(1f, 1f, 1f, 1f);
            //glColor3f(1f, 1f, 1f);
            //glBlendFunc(GL_ZERO, GL_ONE_MINUS_SRC_ALPHA);
            getAnim().render(animate);
           

            
            glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

            //Drawer.refreshColor(place);
            //glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
            //glTranslatef(0, (int) jump, 0);
            glPopMatrix();
        }
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit) {
        if (nLit != null && lit != null) {
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

    int a = 0;  //TYLKO TYMCZASOWE!

    public void update(Place place) {
        if (ctrl.isPressed(MyController.UP)) {
            addSpeed(0, -4, true);
        } else if (ctrl.isPressed(MyController.DOWN)) {
            addSpeed(0, 4, true);
        } else {
            brake(1);
        }
        if (ctrl.isPressed(MyController.LEFT)) {
            addSpeed(-4, 0, true);
        } else if (ctrl.isPressed(MyController.RIGHT)) {
            addSpeed(4, 0, true);
        } else {
            brake(0);
        }
        if (ctrl.isPressed(MyController.SHAKE)) {
            cam.shake();
        }
        if (ctrl.isPressed(MyController.RUN)) {
            setMaxSpeed(16);
        } else {
            setMaxSpeed(8);
        }
        if (ctrl.isClicked(MyController.LIGHT)) {
            setEmits(!emits);
        }
        jump = Math.abs(Methods.xRadius(a * 4, 70));
        a++;
        canMove((int) (hspeed + myHspeed), (int) (vspeed + myVspeed));
        brakeOthers();
    }
}
