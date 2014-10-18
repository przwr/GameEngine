package game.place;

import game.gameobject.GameObject;
import static org.lwjgl.opengl.GL11.*;
import sprites.SpriteSheet;

public abstract class Tile extends GameObject {

    public static int SIZE;
    private SpriteSheet sh;
    private int xSheet;
    private int ySheet;

    public Tile(SpriteSheet sh, int size, boolean isSolid, boolean isEmitter, int xSheet, int ySheet, Place place) {
        SIZE = size;
        this.solid = isSolid;
        this.emitter = isEmitter;
        this.sh = sh;
        this.xSheet = xSheet;
        this.ySheet = ySheet;
        //this.spr = place.getSprite(tex, size, size);
        this.place = place;
    }

    public void render(int flip, int x, int y) {
        glPushMatrix();
        glTranslatef(x, y, 0);
        sh.render(1, xSheet, ySheet);
        //spr.render(flip);
        glPopMatrix();
    }

}
