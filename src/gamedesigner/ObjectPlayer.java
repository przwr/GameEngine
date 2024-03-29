/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamedesigner;

import collision.Figure;
import engine.systemcommunication.Time;
import engine.utilities.*;
import game.gameobject.GUIObject;
import game.gameobject.GameObject;
import game.gameobject.entities.Player;
import game.gameobject.inputs.InputKeyBoard;
import game.place.Place;
import game.place.map.Map;
import game.place.map.MapObjectContainer;
import gamecontent.MyController;
import gamedesigner.designerElements.RoundedTMPBlock;
import gamedesigner.designerElements.TemporaryBlock;
import net.jodk.lang.FastMath;
import net.packets.Update;
import org.lwjgl.input.Keyboard;

import java.util.ArrayList;

import static org.lwjgl.input.Keyboard.*;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author przemek
 */
public class ObjectPlayer extends Player {

    public static int currectDepth;

    private final SimpleKeyboard key;
    private final RandomGenerator rand;
    private int maxTimer;
    private int ix, iy;
    private int xTimer, yTimer;
    private int tileSize;
    private int xStop, yStop;
    private ObjectMap objMap;
    private ObjectPlace objPlace;
    private ObjectUI ui;
    private int blockHeight, tileHeight, mode;
    private boolean roundBlocksMode, alreadyPlaced, alreadyChangedObject;
    private boolean paused, shadow, nightLight, altMode;
    private RoundedTMPBlock rTmpBlock;
    private ArrayList<TemporaryBlock> movingBlock;
    private Point lastRTMPBState = new Point(0, 0);

    public ObjectPlayer(boolean first, String name) {
        super(name);
        this.first = first;
        maxTimer = 7;
        xTimer = 0;
        yTimer = 0;
        key = new SimpleKeyboard();
        rand = RandomGenerator.create();
        initializeController();
        lights = new ArrayList<>();
    }

    private void initializeController() {
        playerController = new MyController(this, null);
        playerController.inputs[0] = new InputKeyBoard(Keyboard.KEY_UP);
        playerController.inputs[1] = new InputKeyBoard(Keyboard.KEY_DOWN);
        playerController.inputs[2] = new InputKeyBoard(Keyboard.KEY_RETURN);
        playerController.inputs[3] = new InputKeyBoard(Keyboard.KEY_ESCAPE);
        playerController.inputs[4] = new InputKeyBoard(Keyboard.KEY_LEFT);
        playerController.inputs[5] = new InputKeyBoard(Keyboard.KEY_RIGHT);
        playerController.initialize();
    }

    @Override
    public void addGui(GUIObject gui) {
        super.addGui(gui);
        if (gui instanceof ObjectUI) {
            ui = (ObjectUI) gui;
        }
    }

    @Override
    public void initializeSetPosition(int width, int height, Place place, int x, int y) {
        initialize(name, 0, 0);
        initialize(width, height, place);
    }

    @Override
    public void initialize(int width, int height, Place place) {
        this.place = place;
        this.online = place.game.online;
        this.setResistance(2);
        this.setEmitter(true);
        setEmits(false);
        tileSize = Place.tileSize;
        objPlace = (ObjectPlace) place;
        setOnTop(true);
    }

    @Override
    protected boolean isCollided(double xMagnitude, double yMagnitude) {
        return isInGame() && collision.isCollideSolid((int) (getXInDouble() + xMagnitude), (int) (getYInDouble() + yMagnitude), map);
    }

    @Override
    protected void move(double xPos, double yPos) {
        int xdelta = (int) xPos;
        int ydelta = (int) yPos;
        double realTimer = (double) maxTimer / Time.getDelta();
        boolean ctrl = key.key(KEY_LCONTROL);
        if (xdelta != 0 && xTimer == 0) {
            ix = Methods.interval(0, ix + xdelta, map.getWidthInTiles() - 1);
            setX(ix * tileSize);
            alreadyPlaced = false;
        }
        if (ydelta != 0 && yTimer == 0) {
            iy = Methods.interval(0, iy + ydelta, map.getHeightInTiles() - 1);
            setY(iy * tileSize);
            alreadyPlaced = false;
        }
        updateAreaPlacement();
        if (key.key(KEY_M) && movingBlock != null) {
            movingBlock.stream().forEach((tmpB) -> tmpB.move(xTimer == 0 ? xdelta * tileSize : 0, yTimer == 0 ? ydelta * tileSize : 0));
        }

        if (mode < ObjectPlace.MODE_VIEWING) {
            if (xTimer == 0 && (!ctrl || roundBlocksMode)) {
                xStop = Methods.interval(0, xStop + xdelta, map.getWidthInTiles());
            }
            if (yTimer == 0 && !ctrl) {
                yStop = Methods.interval(0, yStop + ydelta, map.getHeightInTiles());
            }
        } else {
            xStop = ix;
            yStop = iy;
        }

        ui.setCursorStatus(ix, iy, Math.abs(ix - xStop) + 1, Math.abs(iy - yStop) + 1);
        xTimer++;
        yTimer++;
        if (xTimer >= realTimer) {
            xTimer = 0;
        }
        if (yTimer >= realTimer) {
            yTimer = 0;
        }
    }

    @Override
    public void update() {
        key.keyboardStart();
        if (objPlace.areKeysUsable() && !paused) {
            updateMovement();

            Place.currentCamera.setCameraSpeed(20);
            if (camera != null) {
                camera.setCameraSpeed(20);
                camera.updateSmooth();
            }

            updateModeSpecifics();

            moveBlocksKey();

            if (!key.key(KEY_SPACE) && !key.key(KEY_DELETE)) {
                alreadyPlaced = false;
            }

            if (!alreadyPlaced) {
                if (key.key(KEY_C) && mode == ObjectPlace.MODE_BLOCK && roundBlocksMode) {
                    alreadyPlaced = true;
                    GameObject g = setInstance();
                    if (g != null) {
                        paused = false;
                        RoundedTMPBlock tmp = (RoundedTMPBlock) g;
                        tmp.loadStates(lastRTMPBState);
                        tmp.applyStates();
                    }
                }
                if (key.key(KEY_SPACE)) {
                    alreadyPlaced = true;
                    if (mode == ObjectPlace.MODE_TILE) {
                        setTile();
                    } else {
                        setInstance();
                    }
                }
                if (key.key(KEY_DELETE)) {
                    alreadyPlaced = true;
                    if (mode == ObjectPlace.MODE_TILE) {
                        deleteTile();
                    } else {
                        deleteInstance();
                    }
                }
            }

            if (key.keyPressed(KEY_LMENU)) {
                altMode = !altMode;
            }
            
            if (key.keyPressed(KEY_B)) {
                objMap.changeBlockUsability(ix, iy);
            }

            if (key.keyPressed(KEY_HOME)) {
                objPlace.setCentralPoint(ix, iy);
            }

            if (key.keyPressed(KEY_ADD)) {
                camera.switchZoom();
            }

            if (key.keyPressed(KEY_Q)) {
                if (!shadow && !nightLight) {
                    shadow = true;
                    objPlace.printMessage("Shadow FGTiles mode");
                } else if (shadow) {
                    shadow = false;
                    nightLight = true;
                    objPlace.printMessage("Night-Light FGTiles mode");
                } else {
                    nightLight = false;
                    objPlace.printMessage("Normal FGTiles mode");
                }
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
                if (key.keyPressed(KEY_RETURN)) {
                    paused = false;
                    lastRTMPBState = rTmpBlock.saveStates();
                    rTmpBlock.applyStates();
                }
            }
        }
        key.keyboardEnd();
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    private void moveBlocksKey() {
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
    }

    private void updateMovement() {
        int xPos = 0;
        int yPos = 0;

        maxTimer = key.key(KEY_A) ? 2 : 7;

        if (key.key(KEY_LCONTROL) && key.key(KEY_Z)) {
            xStop = ix;
            yStop = iy;
        }

        if (key.key(KEY_UP)) {
            yPos--;
        } else if (key.key(KEY_DOWN)) {
            yPos++;
        } else {
            yTimer = 0;
        }
        if (key.key(KEY_LEFT)) {
            xPos--;
        } else if (key.key(KEY_RIGHT)) {
            xPos++;
        } else {
            xTimer = 0;
        }

        if (mode == ObjectPlace.MODE_OBJECT && key.key(KEY_LCONTROL)) {
            if (!alreadyChangedObject) {
                ui.changeChosenObject(xPos, yPos);
            }
            if (xPos != 0 || yPos != 0) {
                ui.changeChosenObject(xPos, yPos);
                alreadyChangedObject = true;
            }

        } else {
            if (xPos != 0 || yPos != 0) {
                if (ui.isChanged()) {
                    if (xTimer == 0 && yTimer == 0) {
                        ui.changeCoordinates(xPos, yPos);
                        xTimer = 1;
                        yTimer = 1;
                    }
                } else if (mode == ObjectPlace.MODE_BLOCK && key.key(KEY_LSHIFT)) {
                    if (xTimer == 0 && yTimer == 0) {
                        blockHeight = FastMath.max(0, -yPos + blockHeight);
                        xTimer = 1;
                        yTimer = 1;
                    }
                } else {
                    move(xPos, yPos);
                }
            }
            alreadyChangedObject = false;
        }
    }

    private void updateModeSpecifics() {
        if (mode == ObjectPlace.MODE_TILE) {
            if (key.keyPressed(KEY_PRIOR)) {
                tileHeight++;
            } else if (tileHeight > 0 && key.keyPressed(KEY_NEXT)) {
                tileHeight--;
            }
            currectDepth = tileHeight * tileSize / 2;
            getCamera().setLookingPoint(0, -(tileHeight / 2) * tileSize);
            ui.setChange(key.key(KEY_LSHIFT));
            roundBlocksMode = false;
        } else {
            getCamera().clearLookingPoint();
        }
        if (mode == ObjectPlace.MODE_BLOCK) {
            if (key.keyPressed(KEY_PRIOR)) {
                blockHeight++;
            } else if (blockHeight > 0 && key.keyPressed(KEY_NEXT)) {
                blockHeight--;
            }
            if (key.keyPressed(KEY_R)) {
                roundBlocksMode = !roundBlocksMode;
            }
        }
        if (mode == ObjectPlace.MODE_OBJECT) {
            roundBlocksMode = false;
            ui.setChange(key.key(KEY_LSHIFT));
        }
    }

    private void setTile() {
        int xBegin = Math.min(ix, xStop);
        int yBegin = Math.min(iy, yStop) - tileHeight / 2;
        int xEnd = Math.max(ix, xStop);
        int yEnd = Math.max(iy, yStop) - tileHeight / 2;
        for (int xTemp = xBegin; xTemp <= xEnd; xTemp++) {
            for (int yTemp = yBegin; yTemp <= yEnd; yTemp++) {
                Point p = ui.getCoordinates();
                if (tileHeight == 0 && !shadow && !nightLight) {
                    objMap.addTile(xTemp, yTemp, p.getX(), p.getY(), ui.getSpriteSheet(), altMode);
                } else {
                    objMap.addFGTile(xTemp, yTemp, p.getX(), p.getY(), ui.getSpriteSheet(), tileHeight, altMode, shadow || nightLight, shadow);
                }
            }
        }
    }

    private GameObject setInstance() {
        objPlace.getUndoControl().setUpUndo();
        int xBegin = Math.min(ix, xStop);
        int yBegin = Math.min(iy, yStop);
        if (mode == ObjectPlace.MODE_BLOCK) {
            int xd = (Math.abs(ix - xStop) + 1);
            int yd = (Math.abs(iy - yStop) + 1);
            if (!objMap.checkBlockCollision(xBegin * tileSize, yBegin * tileSize, xd * tileSize, yd * tileSize)) {
                if (roundBlocksMode) {
                    rTmpBlock = new RoundedTMPBlock(xBegin * tileSize, yBegin * tileSize, blockHeight, yd, map);
                    objMap.addObject(rTmpBlock, altMode);
                    paused = true;
                    return rTmpBlock;
                } else {
                    objMap.addObject(new TemporaryBlock(xBegin * tileSize, yBegin * tileSize, blockHeight, xd, yd, map), altMode);
                }
            }
        } else if (mode == ObjectPlace.MODE_OBJECT) {
            GameObject obj = MapObjectContainer.generate(ix * tileSize, iy * tileSize,
                    rand, (byte) ui.getChosenObject(), ui.getData());
            if (obj != null) {
                objMap.addMapObject(obj, ui.getData());
            }
        }
        return null;
    }

    private void deleteTile() {
        objPlace.getUndoControl().setUpUndo();
        int xBegin = Math.min(ix, xStop);
        int yBegin = Math.min(iy, yStop) - tileHeight / 2;
        int xEnd = Math.max(ix, xStop);
        int yEnd = Math.max(iy, yStop) - tileHeight / 2;
        for (int xTemp = xBegin; xTemp <= xEnd; xTemp++) {
            for (int yTemp = yBegin; yTemp <= yEnd; yTemp++) {
                if (tileHeight == 0 && !shadow && !nightLight) {
                    objMap.deleteTile(xTemp, yTemp);
                } else {
                    objMap.deleteFGTile(xTemp, yTemp, tileHeight, shadow || nightLight, shadow);
                }
            }
        }
    }

    private void deleteInstance() {
        objPlace.getUndoControl().setUpUndo();
        int xBegin = Math.min(ix, xStop);
        int yBegin = Math.min(iy, yStop);
        if (mode == ObjectPlace.MODE_BLOCK) {
            int xd = (Math.abs(ix - xStop) + 1);
            int yd = (Math.abs(iy - yStop) + 1);
            objMap.deleteBlocks(xBegin * tileSize, yBegin * tileSize, xd * tileSize, yd * tileSize);
        } else if (mode == ObjectPlace.MODE_OBJECT) {
            objMap.deleteMapObject(ix, iy);
        }
    }

    public void render() {
        Drawer.regularShader.translate(Math.min(ix, xStop) * tileSize, Math.min(iy, yStop) * tileSize);
        int d = 3;
        int xd = (Math.abs(ix - xStop) + 1) * tileSize;
        int yd = (Math.abs(iy - yStop) + 1) * tileSize;
        glBlendFunc(GL_ONE_MINUS_DST_COLOR, GL_ONE_MINUS_SRC_COLOR);
        if (shadow) {
            Drawer.setColorStatic(0.7f, 0.7f, 1f, 1f);
        } else if (nightLight) {
            Drawer.setColorStatic(1f, 0.7f, 0.7f, 1f);
        } else {
            Drawer.setColorStatic(1f, 1f, 1f, 1f);
        }
        if (mode == ObjectPlace.MODE_TILE) {
            if (tileHeight > 0) {
                Drawer.regularShader.translateNoReset(0, -(tileHeight / 2) * tileSize);
            }
            Drawer.drawRectangle(-d, -d, xd + 2 * d, d);
            Drawer.drawRectangle(-d, yd, xd + 2 * d, d);
            Drawer.drawRectangle(-d, 0, d, yd);
            Drawer.drawRectangle(xd, 0, d, yd);
            if (tileHeight > 0) {
                Drawer.drawRectangle(xd / 2, yd / 2, d, tileHeight * tileSize / 2);
                Drawer.drawCircle(xd / 2, yd / 2 + tileHeight * tileSize / 2, (int) (tileSize * 0.3), 10);
            }
        }
        if (mode == ObjectPlace.MODE_BLOCK) {
            if (roundBlocksMode) {
                Drawer.setColorStatic(0f, 0f, 1f, 1f);
            } else {
                Drawer.setColorStatic(1f, 0.78f, 0f, 1f);
            }
            int tmpH = blockHeight * tileSize;
            if (blockHeight == 0) {
                Drawer.drawRectangle(-d, -d, xd + 2 * d, d);
                Drawer.drawRectangle(-d, yd, xd + 2 * d, d);
                Drawer.drawRectangle(-d, 0, d, yd);
                Drawer.drawRectangle(xd, 0, d, yd);
            } else {
                Drawer.drawRectangle(0, -d - tmpH, xd, d);
                Drawer.drawRectangle(0, yd - tmpH, xd, d);
                Drawer.drawRectangle(0, yd, xd, d);
                Drawer.drawRectangle(-d, -tmpH - d, d, tmpH + yd + 2 * d);
                Drawer.drawRectangle(xd, -tmpH - d, d, tmpH + yd + 2 * d);
            }
        }
        if (altMode && mode <= ObjectPlace.MODE_BLOCK) {
            Drawer.drawRing(-tileSize / 3, -tileSize / 3, tileSize / 5, d, 10);
        }
        if (mode == ObjectPlace.MODE_OBJECT) {
            Drawer.drawRing(tileSize / 2, tileSize / 2, tileSize / 4, d, 10);
            Drawer.drawRing(tileSize / 2, tileSize / 2, tileSize / 2, d, 10);
        }

        Drawer.refreshForRegularDrawing();
        Drawer.regularShader.translateNoReset(-Math.min(ix, xStop) * tileSize, -Math.min(iy, yStop) * tileSize);
        if (mode == ObjectPlace.MODE_OBJECT) {
            Drawer.setColorAlpha(0.5f);
            ui.renderChosenObject(Math.min(ix, xStop) * tileSize + tileSize / 2, Math.min(iy, yStop) * tileSize + tileSize / 2);
            Drawer.refreshColor();
        }
    }

    @Override
    public void changeMap(Map newMap, int x, int y) {
        super.changeMap(newMap, x, y);
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
    public void renderShadowLit(Figure figure) {
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(Figure figure) {
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
    }
}
