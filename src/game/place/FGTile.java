/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place;

import engine.Drawer;
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

    private boolean isItWall;   //true - Ściana (bok), false - szczyt "słupa" (góra)
    private boolean lightproof; //Czy światło ma w ogóle szansę na to świecić
    private boolean simpleLighting; //Czy rysuje tekstury (false), czy prostokąty (true)
    private int highness;
    
    public FGTile(SpriteSheet sh, int size, int xSheet, int ySheet, boolean isItWall, Place place) {
        super(sh, size, xSheet, ySheet, place);
        this.isItWall = isItWall;
        this.simpleLighting = true;
    }

    public boolean isItWall() {
        return isItWall;
    }

    public void setItWall(boolean isItWall) {
        this.isItWall = isItWall;
    }

    public boolean isLightproof() {
        return lightproof;
    }

    public void setLightproof(boolean lightproof) {
        this.lightproof = lightproof;
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
    public void renderShadow(int xEffect, int yEffect, boolean isLit) {
        if (simpleLighting) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            if (isLit && !lightproof)
                glColor4f(1f, 1f, 1f, 1f);
            else
                glColor4f(0f, 0f, 0f, 1f);                
            Drawer.drawRectangle(0, 0, sh.getWidth(), sh.getHeight());
            glPopMatrix();
        } else if (nLit != null) {
            glPushMatrix();
            glTranslatef(getX() + xEffect, getY() + yEffect, 0);
            if (isLit) {
                //lit.render(); COŚTAM JASNEGO!
            } else {
                //nLit.render(); COŚTAM CIEMNEGO!
            }
            glPopMatrix();
        }
    }
}
