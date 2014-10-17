package game.place;

import game.gameobject.GameObject;
import sprites.Sprite;
import static org.lwjgl.opengl.GL11.*;

public abstract class Tile extends GameObject {

    public static int SIZE;

    public Tile(String tex, int size, boolean isSolid, boolean isEmitter, Place place) {
        SIZE = size;
        this.solid = isSolid;
        this.emitter = isEmitter;
        this.spr = place.getSprite(tex, size, size);
    }

    public void render(int flip, int x, int y) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        spr.render(flip);
        glPopMatrix();
    }

}
