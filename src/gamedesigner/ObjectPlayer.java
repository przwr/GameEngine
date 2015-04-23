
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import gamedesigner.designerElements.RoundedTMPBlock;
import gamedesigner.designerElements.TemporaryBlock;
import collision.Figure;
import game.gameobject.Player;
import game.place.Place;
import engine.Drawer;
import engine.Methods;
import engine.Point;
import game.Settings;
import game.gameobject.GUIObject;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Map;
import gamedesigner.designerElements.PuzzleLink;
import java.util.ArrayList;
import net.jodk.lang.FastMath;
import net.packets.Update;
import org.lwjgl.input.Keyboard;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.input.Keyboard.*;

/**
 *
 * @author przemek
 */
public class ObjectPlayer extends Player {

    private int maxtimer;
    private int ix, iy;
    private int xtimer, ytimer;
    private int tileSize;
    private int xStop, yStop;

    private ObjectMap objMap;
    private ObjectPlace objPlace;
    private ObjectUI ui;
    private final SimpleKeyboard key;

    private int blockHeight, radius, mode;
    private boolean roundBlocksMode;
    private boolean paused;

    private RoundedTMPBlock rTmpBlock;
    private ArrayList<TemporaryBlock> movingBlock;

    public ObjectPlayer(boolean first, String name) {
        super(name);
        this.first = first;
        maxtimer = 7;
        xtimer = 0;
        ytimer = 0;
        radius = 1;
        key = new SimpleKeyboard();
        initializeController();
    }

    private void initializeController() {
        controler = new ObjectController(this);
        controler.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
        controler.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
        controler.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
        controler.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        controler.inputs[6] = new InputKeyBoard(Keyboard.KEY_END);
        controler.initialize();
    }

    @Override
    public void addGui(GUIObject gui) {
        super.addGui(gui);
        if (gui instanceof ObjectUI) {
            ui = (ObjectUI) gui;
        }
    }

    @Override
    public void initializeSetPosition(int xStart, int yStart, int width, int height, Place place, int x, int y) {
        initialize(name, x, y);
        initialize(yStart, yStart, width, height, place);
    }

    @Override
    public void initialize(int xStart, int yStart, int width, int height, Place place) {
        this.place = place;
        this.online = place.game.online;
        this.width = width;
        this.height = height;
        this.xStart = xStart;
        this.yStart = yStart;
        this.setResistance(2);
        this.emitter = true;
        emits = false;
        tileSize = Place.tileSize;
        objPlace = (ObjectPlace) place;
        onTop = true;
    }

    @Override
    protected boolean isColided(int xMagnitude, int yMagnitude) {
        if (isInGame()) {
            return collision.isCollideSolid(getX() + xMagnitude, getY() + yMagnitude, map);
        }
        return false;
    }

    @Override
    protected void move(int xPosition, int yPosition) {
        boolean cltr = key.key(KEY_LCONTROL);
        if (xtimer == 0) {
            ix = Methods.interval(0, ix + xPosition, map.getWidthInTIles());
            setX(ix * tileSize);
        }
        if (ytimer == 0) {
            iy = Methods.interval(0, iy + yPosition, map.getHeightInTiles());
            setY(iy * tileSize);
        }
        updateAreaPlacement();
        if (key.key(KEY_M) && movingBlock != null) {            
            movingBlock.stream().forEach((tmpb) -> {
                tmpb.move(xtimer == 0 ? xPosition * tileSize : 0, ytimer == 0 ? yPosition * tileSize : 0);
                
            });
        }

        if (mode < 2) {
            if (xtimer == 0 && (!cltr || roundBlocksMode)) {
                xStop = Methods.interval(0, xStop + xPosition, map.getWidthInTIles());
            }
            if (ytimer == 0 && !cltr) {
                yStop = Methods.interval(0, yStop + yPosition, map.getHeightInTiles());
            }
        } else {
            xStop = ix;
            yStop = iy;
        }

        ui.setCursorStatus(ix, iy, Math.abs(ix - xStop) + 1, Math.abs(iy - yStop) + 1);
        if (camera != null) {
            camera.update();
        }
        xtimer++;
        ytimer++;
        if (xtimer >= maxtimer) {
            xtimer = 0;
        }
        if (ytimer >= maxtimer) {
            ytimer = 0;
        }
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public void update() {
        key.keyboardStart();
        if (objPlace.areKeysUsable() && !paused) {
            int xPos = 0;
            int yPos = 0;

            maxtimer = key.key(KEY_A) ? 2 : 7;

            if (key.key(KEY_LCONTROL) && key.key(KEY_Z)) {
                xStop = ix;
                yStop = iy;
            }

            if (key.key(KEY_UP)) {
                yPos--;
            } else if (key.key(KEY_DOWN)) {
                yPos++;
            } else {
                ytimer = 0;
            }
            if (key.key(KEY_LEFT)) {
                xPos--;
            } else if (key.key(KEY_RIGHT)) {
                xPos++;
            } else {
                xtimer = 0;
            }

            if (mode == 0) {
                ui.setChange(key.key(KEY_LSHIFT));
                roundBlocksMode = false;
            }
            if (mode == 1 && key.keyPressed(KEY_R)) {
                roundBlocksMode = !roundBlocksMode;
            }
            if (mode == 3) {
                roundBlocksMode = false;
            }

            if (key.key(KEY_M)) {
                if (movingBlock == null) {
                    objPlace.getUndoControl().setUpUndo();
                    int xBegin = Math.min(ix, xStop);
                    int yBegin = Math.min(iy, yStop);
                    int xd = (Math.abs(ix - xStop) + 1);
                    int yd = (Math.abs(iy - yStop) + 1);
                    movingBlock = objMap.getBlock(xBegin * tileSize, yBegin * tileSize, xd * tileSize, yd * tileSize);
                }
            } else {
                movingBlock = null;
            }

            if (xPos != 0 || yPos != 0) {
                if (ui.isChanged()) {
                    if (xtimer == 0 && ytimer == 0) {
                        ui.changeCoordinates(xPos, yPos);
                        xtimer = 1;
                        ytimer = 1;
                    }
                } else if (mode == 1 && key.key(KEY_LSHIFT)) {
                    if (xtimer == 0 && ytimer == 0) {
                        blockHeight = FastMath.max(0, -yPos + blockHeight);
                        xtimer = 1;
                        ytimer = 1;
                    }
                } else if (mode == 3 && key.key(KEY_LSHIFT)) {
                    if (xtimer == 0 && ytimer == 0) {
                        radius = Methods.interval(1, radius - yPos, 20);
                        xtimer = 1;
                        ytimer = 1;
                    }
                } else {
                    move(xPos, yPos);
                }
            }

            if (key.keyPressed(KEY_SPACE) || key.keyPressed(KEY_LMENU)) {
                objPlace.getUndoControl().setUpUndo();
                int xBegin = Math.min(ix, xStop);
                int yBegin = Math.min(iy, yStop);
                int xEnd = Math.max(ix, xStop);
                int yEnd = Math.max(iy, yStop);
                if (mode == 0) {
                    for (int xTemp = xBegin; xTemp <= xEnd; xTemp++) {
                        for (int yTemp = yBegin; yTemp <= yEnd; yTemp++) {
                            Point p = ui.getCoordinates();
                            objMap.addTile(xTemp, yTemp, p.getX(), p.getY(), ui.getSpriteSheet(), key.key(KEY_LMENU));
                        }
                    }
                } else if (mode == 1) {
                    int xd = (Math.abs(ix - xStop) + 1);
                    int yd = (Math.abs(iy - yStop) + 1);
                    if (!objMap.checkBlockCollision(xBegin * tileSize, yBegin * tileSize, xd * tileSize, yd * tileSize)) {
                        if (roundBlocksMode) {
                            rTmpBlock = new RoundedTMPBlock(xBegin * tileSize, yBegin * tileSize, blockHeight, yd, map);
                            objMap.addObject(rTmpBlock, key.key(KEY_LMENU));
                            paused = true;
                        } else {
                            objMap.addObject(new TemporaryBlock(xBegin * tileSize, yBegin * tileSize, blockHeight, xd, yd, map), key.key(KEY_LMENU));
                        }
                    }
                } else if (mode == 3) {
                    PuzzleLink pl = new PuzzleLink(ix * tileSize, iy * tileSize, radius, objPlace);
                    objMap.addObject(pl, false);
                }
            }

            if (key.keyPressed(KEY_DELETE)) {
                objPlace.getUndoControl().setUpUndo();
                int xBegin = Math.min(ix, xStop);
                int yBegin = Math.min(iy, yStop);
                int xEnd = Math.max(ix, xStop);
                int yEnd = Math.max(iy, yStop);
                if (mode == 0) {
                    for (int xTemp = xBegin; xTemp <= xEnd; xTemp++) {
                        for (int yTemp = yBegin; yTemp <= yEnd; yTemp++) {
                            objMap.deleteTile(xTemp, yTemp);
                        }
                    }
                } else if (mode == 1) {
                    int xd = (Math.abs(ix - xStop) + 1);
                    int yd = (Math.abs(iy - yStop) + 1);
                    objMap.deleteBlocks(xBegin * tileSize, yBegin * tileSize, xd * tileSize, yd * tileSize);
                } else if (mode == 3) {
                    objMap.deleteLink(ix * tileSize, iy * tileSize);
                }
            }

            if (key.keyPressed(KEY_B)) {
                objMap.changeBlockUsability(ix, iy);
            }

            if (key.keyPressed(KEY_HOME)) {
                objPlace.setCentralPoint(ix, iy);
            }
        } else if (paused) {
            if (roundBlocksMode) {
                if (key.keyPressed(KEY_UP)) {
                    rTmpBlock.changeLowerState(-1);
                }
                if (key.keyPressed(KEY_DOWN)) {
                    rTmpBlock.changeLowerState(1);
                }
                if (key.keyPressed(KEY_LEFT)) {
                    rTmpBlock.changeUpperState(1);
                }
                if (key.keyPressed(KEY_RIGHT)) {
                    rTmpBlock.changeUpperState(-1);
                }
                if (key.keyPressed(KEY_RETURN) || key.keyPressed(KEY_SPACE)) {
                    paused = false;
                    rTmpBlock.applyStates();
                }
            }
        }
        key.keyboardEnd();
    }

    @Override
    public void render(int xEffect, int yEffect) {
        glPushMatrix();
        int d = 3;
        int xd = (Math.abs(ix - xStop) + 1) * tileSize;
        int yd = (Math.abs(iy - yStop) + 1) * tileSize;
        glTranslatef(xEffect, yEffect, 0);
        if (Settings.scaled) {
            glScaled(Place.getCurrentScale(), Place.getCurrentScale(), 1);
        }
        glTranslatef(Math.min(ix, xStop) * tileSize, Math.min(iy, yStop) * tileSize, 0);

        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
        glColor4f(1f, 1f, 1f, 1f);
        Drawer.setCentralPoint();
        if (mode == 0) {
            Drawer.drawRectangle(-d, -d, xd + 2 * d, d);
            Drawer.drawRectangle(0, yd + d, xd + 2 * d, d);
            Drawer.drawRectangle(0, -yd, d, yd);
            Drawer.drawRectangle(xd + d, 0, d, yd);
        }
        if (mode == 1) {
            if (roundBlocksMode) {
                glColor3f(0f, 0f, 1f);
            } else {
                glColor4f(1f, 0.78f, 0f, 1f);
            }
            int tmpH = blockHeight * tileSize;
            if (blockHeight == 0) {
                Drawer.drawRectangle(-d, -d, xd + 2 * d, d);
                Drawer.drawRectangle(0, yd + d, xd + 2 * d, d);
                Drawer.drawRectangle(0, -yd, d, yd);
                Drawer.drawRectangle(xd + d, 0, d, yd);
            } else {
                Drawer.drawRectangle(-d, -d - tmpH, xd + 2 * d, d);
                Drawer.drawRectangle(d, yd + d, xd, d);
                Drawer.drawRectangle(0, tmpH, xd, d);
                Drawer.drawRectangle(-d, d, d, -tmpH - yd - d);
                Drawer.drawRectangle(xd + d, 0, d, -tmpH - yd - d);
            }
        }
        if (key.key(KEY_LMENU) && mode <= 1) {
            Drawer.returnToCentralPoint();
            Drawer.drawRing(-tileSize / 3, -tileSize / 3, tileSize / 5, d, 10);
        }
        if (mode == 3) {
            Drawer.drawRing(tileSize / 2, tileSize / 2, tileSize / 4, d, 10);
            int complex = radius * 2 + 10;
            if (radius % 2 == 1) {
                Drawer.drawRing(0, 0, radius * tileSize / 2, d, complex);
            } else {
                Drawer.drawRing(-tileSize / 2, -tileSize / 2, radius * tileSize / 2, d, complex);
            }
        }

        Drawer.refreshForRegularDrawing();
        glPopMatrix();
    }

    @Override
    public void changeMap(Map newMap) {
        super.changeMap(newMap);
        if (camera != null) {
            camera.setMap(newMap);
        }
        objMap = (ObjectMap) newMap;
    }

    @Override
    public void sendUpdate() {
    }

    @Override
    public void updateOnline() {
    }

    @Override
    public void updateRest(Update update) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadowLit(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure) {
    }

    @Override
    public void renderShadow(int xEffect, int yEffect, Figure figure, int xStart, int xEnd) {
    }
}
