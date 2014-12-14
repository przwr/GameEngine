/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import collision.Figure;
import collision.Rectangle;
import engine.Drawer;
import static org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA;
import static org.lwjgl.opengl.GL11.GL_SRC_COLOR;
import static org.lwjgl.opengl.GL11.glBlendFunc;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class FGTile extends Tile {

    private int highness;

    public FGTile(SpriteSheet sh, int size, int xSheet, int ySheet, boolean isItWall, int height, int shadowH, AbstractPlace place) {
        super(sh, size, xSheet, ySheet, place);
        this.simpleLighting = true;
        this.solid = isItWall;
        setCollision(new Rectangle(0, height, size, size, isItWall, isItWall, shadowH, this));
    }

    public boolean isSimpleLighting() {
        return simpleLighting;
    }

    public void setSimpleLighting(boolean simpleLighting) {
        this.simpleLighting = simpleLighting;
    }

    public int getHighness() {
        return highness;
    }

    public void setHighness(int highness) {
        this.highness = highness;
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, boolean isLit, float color, Figure f) {
        glPushMatrix();
        glTranslatef(getX() + xEffect, getY() - collision.shadowHeight() + yEffect, 0);
        if (simpleLighting) {
            if (isLit) {
                glColor3f(color, color, color);
            } else {
                glColor3f(0f, 0f, 0f);
            }
            Drawer.drawRectangle(0, 0, collision.getWidth(), collision.getHeight() + collision.shadowHeight());
            glColor3f(1f, 1f, 1f);
        } else if (sprite != null) {
            if (isLit) {
                Drawer.drawShapeInColor(sprite, color, color, color);
            } else {
                Drawer.drawShapeInBlack(sprite);
            }
            glBlendFunc(GL_SRC_COLOR, GL_ONE_MINUS_SRC_ALPHA);
        }
        glPopMatrix();
    }
}
