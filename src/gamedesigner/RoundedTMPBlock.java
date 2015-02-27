/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import engine.Drawer;
import game.Settings;
import game.place.Map;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;

/**
 *
 * @author Wojtek
 */
public class RoundedTMPBlock extends TemporaryBlock {

    private int upperState;
    private int lowerState;

//    Block block = Block.createRound(8 * tileSize, 8 * tileSize, 2 * tileSize, 2 * tileSize, 0);
//    ForegroundTile fg = ForegroundTile.createRoundWall(place.getSpriteSheet("testy"), tileSize, 0, 3);
//    block.addForegroundTile(fg);
//    block.pushCorner(RoundRectangle.LEFT_TOP, tileSize, 18, 18);
    public RoundedTMPBlock(int x, int y, int upHeight, int height, Map map) {
        super(x, y, upHeight, 1, height, map);
        this.upperState = 3;
        this.lowerState = 3;
        complete = true;
    }

    public void changeUpperState() {
        upperState++;
        if (upperState == 7) {
            upperState = 0;
        }
    }

    public void changeLowerState() {
        lowerState++;
        if (lowerState == 7) {
            lowerState = 0;
        }
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        glTranslatef(xEffect, yEffect, 0);
        if (Settings.scaled) {
            glScaled(Settings.scale, Settings.scale, 1);
        }
        glTranslatef(getX(), getY(), 0);
        int mode = objPlace.getMode();
        if (mode != 2) {
            int d = 2;
            Drawer.refreshColor();
            int tmpH = upHeight * tile;
            if (mode == 1) {
                Drawer.drawRectangle(0, -tmpH, width, height);
            } else {
                glTranslatef(0, -tmpH, 0);
            }
            if (upHeight == 0) {
                glColor3f(0f, 0f, 1f);
                Drawer.drawRectangle(0, 0, width, d);
                Drawer.drawRectangle(0, height - d, width, d);
                Drawer.drawRectangle(0, -height + d, d, height);
                Drawer.drawRectangle(width - d, 0, d, height);
            } else {
                if (mode == 1) {
                    glColor3f(0.9f, 0.9f, 0.9f);
                    Drawer.drawRectangle(0, height, width, tmpH);
                    glTranslatef(0, -height, 0);
                }
                glColor3f(0f, 0f, 1f);
                Drawer.drawRectangle(0, 0, width, d);
                Drawer.drawRectangle(0, height - d, width, d);
                Drawer.drawRectangle(0, tmpH, width, d);
                Drawer.drawRectangle(0, 0, d, -tmpH - height + d);
                Drawer.drawRectangle(width - d, 0, d, -tmpH - height + d);

                Drawer.setCentralPoint();
                switch (lowerState) {
                    case 1:
                        Drawer.drawBow(-tile + d, -tile + d, tile - 1, d + 1, 0, 90, 10);
                        Drawer.drawBow(0, -tmpH, tile - 1, d + 1, 0, 90, 10);
                        break;
                    case 2:
                        Drawer.drawLineWidth(-tile + d, d / 2, tile, -tile, 2 * d);
                        Drawer.drawLineWidth(0, -tmpH, tile, -tile, 2 * d);
                        break;
                    case 3:
                        Drawer.drawBow(0, 0, tile - 1, d + 1, 180, 270, 10);
                        Drawer.drawBow(0, -tmpH, tile - 1, d + 1, 180, 270, 10);
                        break;
                    case 4:
                        Drawer.drawBow(0, -tile + d, tile - 1, d + 1, 90, 180, 10);
                        Drawer.drawBow(0, -tmpH, tile - 1, d + 1, 90, 180, 10);
                        break;
                    case 5:
                        Drawer.drawLineWidth(0, d / 2, -tile, -tile, 2 * d);
                        Drawer.drawLineWidth(0, -tmpH, -tile, -tile, 2 * d);
                        break;
                    case 6:
                        Drawer.drawBow(-tile + d, 0, tile - 1, d + 1, 270, 360, 10);
                        Drawer.drawBow(0, -tmpH, tile - 1, d + 1, 270, 360, 10);
                        break;
                }
                Drawer.returnToCentralPoint();
                if (yTiles > 1) {
                    switch (upperState) {
                        case 1:
                            Drawer.drawBow(-tile + d, -height - tmpH + tile - d, tile - 1, d + 1, 270, 360, 10);
                            break;
                        case 2:
                            Drawer.drawLineWidth(0, -height - tmpH + tile + d / 2, -tile + d, -tile + d, 2 * d);
                            break;
                        case 3:
                            Drawer.drawBow(0, -height - tmpH + d, tile - 1, d + 1, 90, 180, 10);
                            break;
                        case 4:
                            Drawer.drawBow(0, -height - tmpH + tile - d, tile - 1, d + 1, 180, 270, 10);
                            break;
                        case 5:
                            Drawer.drawLineWidth(-tile + d, -height - tmpH + tile + d / 2, tile, -tile, 2 * d);
                            break;
                        case 6:
                            Drawer.drawBow(-tile + d, -height - tmpH + d, tile - 1, d + 1, 0, 90, 10);
                            break;
                    }
                }
            }
        }
        Drawer.refreshForRegularDrawing();
        glPopMatrix();
    }
}
