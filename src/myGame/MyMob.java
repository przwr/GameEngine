/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package myGame;

import engine.Methods;
import game.gameobject.Mob;
import game.place.Place;
import static org.lwjgl.opengl.GL11.GL_ADD;
import static org.lwjgl.opengl.GL11.GL_MODULATE;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV;
import static org.lwjgl.opengl.GL11.GL_TEXTURE_ENV_MODE;
import static org.lwjgl.opengl.GL11.GL_ZERO;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTexEnvi;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author przemek
 */
public class MyMob extends Mob {

    public MyMob(int x, int y, int startX, int startY, int width, int height, int sx, int sy, int speed, int range, String name, Place place, boolean solid, double SCALE) {
        super(x, y, startX, startY, width, height, sx, sy, speed, range, name, place, solid, SCALE);
    }

    @Override
    public void update(Place place) {
        if (prey != null && ((MyPlayer) prey).getPlace() != null) {
            chase(prey);
            if (Methods.PointDistance(getX(), getY(), prey.getX(), prey.getY()) > range * 1.5) {
                prey = null;
            }
        } else {
            look(place.players);
            brake(2);
        }
        canMove((int) (hspeed + myHspeed), (int) (vspeed + myVspeed));
        brakeOthers();
    }
}
