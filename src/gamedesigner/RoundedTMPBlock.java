/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Block;
import collision.RoundRectangle;
import engine.Drawer;
import game.Settings;
import game.place.ForegroundTile;
import game.place.Map;
import static org.lwjgl.opengl.GL11.glColor3f;
import static org.lwjgl.opengl.GL11.glPopMatrix;
import static org.lwjgl.opengl.GL11.glPushMatrix;
import static org.lwjgl.opengl.GL11.glScaled;
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class RoundedTMPBlock extends TemporaryBlock {

    private int upperState;
    private int lowerState;
    protected boolean complete;

    public RoundedTMPBlock(int x, int y, int upHeight, int height, Map map) {
        super(x, y, upHeight, 1, height, map);
        this.upperState = 0;
        this.lowerState = 0;
        complete = false;
    }

    @Override
    protected ForegroundTile createTile(SpriteSheet texture, int y, int tile, int xSheet, int ySheet, int level, boolean altMode) {
        ForegroundTile fgt;
        int alt = objPlace.isAltMode() ? 0 : 1;
        if (level + 1 - alt <= upHeight) {
            if (y * tile == this.y + height - tile) {
                fgt = ForegroundTile.createRoundWall(texture, tile, xSheet, ySheet);
            } else {
                if (level + 1 <= upHeight) {
                    fgt = ForegroundTile.createWall(texture, tile, xSheet, ySheet);
                } else {
                    fgt = ForegroundTile.createWall(texture, tile, xSheet, ySheet);
                }
            }
        } else {
            fgt = ForegroundTile.createOrdinaryShadowHeight(texture, tile, xSheet, ySheet, level * tile);
            if (y * tile == this.y - upHeight * tile || y * tile == this.y + height - (upHeight + 1) * tile) {
                fgt.setSimpleLighting(false);
            }
        }
        return fgt;
    }

    public void pushCorner(int corner, int xDelta, int yDelta) {
        block.pushCorner(corner, tile, xDelta, yDelta);
        complete = true;
    }

    public void changeUpperState(int change) {
        upperState += change;
        if (upperState > 6) {
            upperState = 0;
        } else if (upperState < 0) {
            upperState = 6;
        }
    }

    public void changeLowerState(int change) {
        lowerState += change;
        if (lowerState > 6) {
            lowerState = 0;
        } else if (lowerState < 0) {
            lowerState = 6;
        }
    }

    public void setStates(int corner, int xChange, int yChange) {
        int sum = xChange + yChange;
        switch (corner) {
            case 0:
                if (upperState == 0) {
                    if (sum == tile) {
                        upperState = 5;
                    } else if (sum > tile) {
                        upperState = 6;
                    } else {
                        upperState = 4;
                    }
                }
                break;
            case 1:
                if (lowerState == 0) {
                    if (sum == tile) {
                        lowerState = 5;
                    } else if (sum > tile) {
                        lowerState = 6;
                    } else {
                        lowerState = 4;
                    }
                }
                break;
            case 2:
                if (lowerState == 0) {
                    if (sum == tile) {
                        lowerState = 2;
                    } else if (sum > tile) {
                        lowerState = 3;
                    } else {
                        lowerState = 1;
                    }
                }
                break;
            case 3:
                if (upperState == 0) {
                    if (sum == tile) {
                        upperState = 2;
                    } else if (sum > tile) {
                        upperState = 3;
                    } else {
                        upperState = 1;
                    }
                }
                break;
        }
    }

    public void applyStates() {
        switch (upperState) {
            case 1:
                block.pushCorner(RoundRectangle.RIGHT_TOP, tile, (int) (tile * 0.292), (int) (tile * 0.292));
                break;
            case 2:
                block.pushCorner(RoundRectangle.RIGHT_TOP, tile, (int) (tile * 0.5), (int) (tile * 0.5));
                break;
            case 3:
                block.pushCorner(RoundRectangle.RIGHT_TOP, tile, (int) (tile * 0.707), (int) (tile * 0.707));
                break;
            case 4:
                block.pushCorner(RoundRectangle.LEFT_TOP, tile, (int) (tile * 0.292), (int) (tile * 0.292));
                break;
            case 5:
                block.pushCorner(RoundRectangle.LEFT_TOP, tile, (int) (tile * 0.5), (int) (tile * 0.5));
                break;
            case 6:
                block.pushCorner(RoundRectangle.LEFT_TOP, tile, (int) (tile * 0.707), (int) (tile * 0.707));
                break;
        }
        switch (lowerState) {
            case 1:
                block.pushCorner(RoundRectangle.RIGHT_BOTTOM, tile, (int) (tile * 0.292), (int) (tile * 0.292));
                break;
            case 2:
                block.pushCorner(RoundRectangle.RIGHT_BOTTOM, tile, (int) (tile * 0.5), (int) (tile * 0.5));
                break;
            case 3:
                block.pushCorner(RoundRectangle.RIGHT_BOTTOM, tile, (int) (tile * 0.707), (int) (tile * 0.707));
                break;
            case 4:
                block.pushCorner(RoundRectangle.LEFT_BOTTOM, tile, (int) (tile * 0.292), (int) (tile * 0.292));
                break;
            case 5:
                block.pushCorner(RoundRectangle.LEFT_BOTTOM, tile, (int) (tile * 0.5), (int) (tile * 0.5));
                break;
            case 6:
                block.pushCorner(RoundRectangle.LEFT_BOTTOM, tile, (int) (tile * 0.707), (int) (tile * 0.707));
                break;
        }
        complete = true;
    }

    @Override
    public void createBlock() {
        block = Block.createRound((int) x, (int) y, width, height, (upHeight - yTiles) * tile);
        map.addBlock(block);
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
                if (!complete) {
                    glColor3f(1f, 1f, 1f);
                    Drawer.returnToCentralPoint();
                    Drawer.drawLineWidth(-tile / 2 + d, -height - tmpH - tile / 2 - d, tile / 3, tile / 3, 2 * d);
                    Drawer.drawLineWidth(0, 0, -tile / 3, tile / 3, 2 * d);
                    Drawer.returnToCentralPoint();
                    Drawer.drawLineWidth(-tile / 2 + d, tile / 2 + d, tile / 3, -tile / 3, 2 * d);
                    Drawer.drawLineWidth(0, 0, -tile / 3, -tile / 3, 2 * d);
                    Drawer.returnToCentralPoint();
                    Drawer.drawLineWidth(-tile * 3 / 2 + d, -height - tmpH + tile / 2 - d, tile / 3, tile / 3, 2 * d);
                    Drawer.drawLineWidth(0, 0, tile / 3, -tile / 3, 2 * d);
                    Drawer.returnToCentralPoint();
                    Drawer.drawLineWidth(tile / 2, -height - tmpH + tile / 2 - d, -tile / 3, tile / 3, 2 * d);
                    Drawer.drawLineWidth(0, 0, -tile / 3, -tile / 3, 2 * d);
                }
            }
        }
        Drawer.refreshForRegularDrawing();
        glPopMatrix();
    }
}
