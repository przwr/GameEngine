package gamecontent.environment;

import collision.Figure;
import engine.utilities.Drawer;
import engine.utilities.RandomGenerator;
import game.Settings;
import game.gameobject.GameObject;
import game.place.map.Area;
import gamedesigner.ObjectPlace;
import org.lwjgl.opengl.Display;
import sprites.fbo.FrameBufferObject;
import sprites.fbo.MultiSampleFrameBufferObject;
import sprites.fbo.RegularFrameBufferObject;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;

/**
 * Created by przemek on 30.11.15.
 */
public class GrassClump extends GameObject {

    public final static byte CORNER_DOWN_LEFT = 0, CORNER_UP_LEFT = 1, CORNER_UP_RIGHT = 2, CORNER_DOWN_RIGHT = 3;

    public static Map<String, FrameBufferObject> fbos = new HashMap<>();
    public static List<GrassClump> instances = new ArrayList();

    public static RandomGenerator random = RandomGenerator.create();

    Grass[] grasses;
    int xBladesCount, yBladesCount, bladeWidth, bladeHeight, xRadius, yRadius, ySpacing, xCount, yCount, xCentering, grassWidth, curve, corner = -1;
    boolean updateGrass, added;

    FrameBufferObject fbo;

    private GrassClump(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        setUp(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight, 0);
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                grasses[i * xCount + j] = Grass.create(x + xCentering + j * grassWidth, y + (i + 1) * ySpacing, xBladesCount, yBladesCount, bladeWidth,
                        bladeHeight);
            }
        }
        instances.add(this);
    }

    private GrassClump(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight, int curve) {
        this.curve = curve;
        if (xBladesCount * 2 < yCount / curve) {
            xBladesCount = yCount / curve / 2;
        }
        setUp(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight, 100 + curve * 10);
        int middle1 = yCount / 2, middle2 = yCount / 2, modXBladesCount, xCenter;
        if (yCount % 2 == 0) {
            middle1 -= 1;
        }
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                modXBladesCount = xBladesCount;
                xCenter = xCentering;
                if (xCount > 1) {
                    if (j == 0) {
                        modXBladesCount -= (i <= middle1 ? (middle1 - i) : (i - middle2)) / curve;
                        xCenter = xCentering * 2 - modXBladesCount * Math.round((bladeWidth - 2) / 2f);
                    } else if (j == xCount - 1) {
                        modXBladesCount -= (i <= middle1 ? (middle1 - i) : (i - middle2)) / curve;
                        xCenter = modXBladesCount * Math.round((bladeWidth - 2) / 2f);
                    }
                }
                grasses[i * xCount + j] = Grass.create(x + xCenter + j * (grassWidth), y + (i + 1) * ySpacing, modXBladesCount, yBladesCount, bladeWidth,
                        bladeHeight);
            }
        }
        instances.add(this);
    }

    private GrassClump(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight, int curve, int corner) {
        this.curve = curve;
        this.corner = corner;
        if (xBladesCount * 2 < yCount / curve) {
            xBladesCount = yCount / curve / 2;
        }
        setUp(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight, 200 + curve * 10 + corner);
        int middle1 = yCount / 2, middle2 = yCount / 2, modXBladesCount, xCenter;
        if (yCount % 2 == 0) {
            middle1 -= 1;
        }
        int xChange = (xCount * xBladesCount) / yCount;
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                modXBladesCount = xBladesCount;
                if (corner == CORNER_DOWN_LEFT) {
                    modXBladesCount -= xChange * i - j * xBladesCount;
                    modXBladesCount += (yCount / 2 - (i <= middle1 ? (middle1 - i) : (i - middle2))) * curve;
                    if (modXBladesCount > xBladesCount) {
                        modXBladesCount = xBladesCount;
                    }
                    xCenter = xCentering + Math.round((xBladesCount - modXBladesCount) * bladeWidth * 0.375f);
                } else if (corner == CORNER_UP_RIGHT) {
                    modXBladesCount = xChange * (i + 1) - j * xBladesCount;
                    modXBladesCount += (yCount / 2 - (i <= middle1 ? (middle1 - i) : (i - middle2))) * curve;
                    if (modXBladesCount > xBladesCount) {
                        modXBladesCount = xBladesCount;
                    }
                    xCenter = xCentering - Math.round((xBladesCount - modXBladesCount) * bladeWidth * 0.375f);
                } else if (corner == CORNER_UP_LEFT) {
                    modXBladesCount -= xChange * (yCount - i - 1) - j * xBladesCount;
                    modXBladesCount += (yCount / 2 - (i <= middle1 ? (middle1 - i) : (i - middle2))) * curve;
                    if (modXBladesCount > xBladesCount) {
                        modXBladesCount = xBladesCount;
                    }
                    xCenter = xCentering + Math.round((xBladesCount - modXBladesCount) * bladeWidth * 0.375f);
                } else {    // down right
                    modXBladesCount = xChange * (yCount - i) - j * xBladesCount;
                    modXBladesCount += (yCount / 2 - (i <= middle1 ? (middle1 - i) : (i - middle2))) * curve;
                    if (modXBladesCount > xBladesCount) {
                        modXBladesCount = xBladesCount;
                    }
                    xCenter = xCentering - Math.round((xBladesCount - modXBladesCount) * bladeWidth * 0.375f);
                }
                if (modXBladesCount > 0) {
                    grasses[i * xCount + j] = Grass.create(x + xCenter + j * grassWidth, y + (i + 1) * ySpacing, modXBladesCount, yBladesCount, bladeWidth,
                            bladeHeight);
                }
            }
        }
        instances.add(this);
    }

    public static GrassClump createRectangle(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        return new GrassClump(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight);
    }

    public static GrassClump createRound(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight) {
        return new GrassClump(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight, 1);
    }

    public static GrassClump createCorner(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight, int
            corner) {
        return new GrassClump(x, y, xCount, yCount, xBladesCount, yBladesCount, bladeWidth, bladeHeight, 1, corner);
    }

    public static boolean allGenerated() {
        for (GrassClump clump : instances) {
            clump.preRender();
        }
        Iterator it = fbos.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            if (!((FrameBufferObject) pair.getValue()).generated) {
                return false;
            }
        }
        instances.clear();
        return true;
    }

    private void setUp(int x, int y, int xCount, int yCount, int xBladesCount, int yBladesCount, int bladeWidth, int bladeHeight, int type) {
        initialize("GrassClump", x, y);
        this.xCount = xCount;
        this.yCount = yCount;
        this.xBladesCount = xBladesCount;
        this.yBladesCount = yBladesCount;
        this.bladeWidth = bladeWidth;
        this.bladeHeight = bladeHeight;
        grassWidth = (bladeWidth - 2) * xBladesCount;
        xRadius = grassWidth / 2 * xCount;
        yRadius = 4 * yCount;
        grasses = new Grass[xCount * yCount];
        ySpacing = 8;
        xCentering = Math.round(xBladesCount * (bladeWidth - 2) / 2f);
        int fboWidth = xRadius * 2 + 4 + (xCount - 1) * xCentering;
        int fboHeight = yRadius * 2 + bladeHeight + 8;
        int ins = random.random(8);
        String grassClumpCode = xCount + "-" + yCount + "-" + xBladesCount + "-" + yBladesCount + "-" + bladeWidth + "-" + bladeHeight + "-" + type
                + "-" + ins;
        int seed = ins * 101653 + type * 104729 + 1121104729;
        fbo = fbos.get(grassClumpCode);
        if (fbo == null) {
            fbo = (Settings.samplesCount > 0) ? new MultiSampleFrameBufferObject(fboWidth, fboHeight)
                    : new RegularFrameBufferObject(fboWidth, fboHeight);
            fbos.put(grassClumpCode, fbo);
        }
        if (Grass.random == null) {
            Grass.random = RandomGenerator.create();
        }
        Grass.random.resetWithSeed(seed);
        depth = yCount * yBladesCount;
    }

    private void updateGrasses() {
        if (map != null) {
            boolean objectMode = map.place instanceof ObjectPlace;
            updateGrass = false;
            for (int i = objectMode ? 1 : 0; i < this.map.place.getPlayersCount(); i++) {
                GameObject player = map.place.players[i];
                if (player.getFloatHeight() < bladeHeight && Math.abs(getX() + xRadius - player.getX()) < xRadius + player.getCollision().getWidthHalf()
                        && Math.abs(getY() + yRadius - player.getY()) < yRadius + player.getCollision().getHeightHalf()) {
                    updateGrass = true;
                    break;
                }
            }
            if (!objectMode) {
                Area area = map.getArea(this.area);
                if (!updateGrass) {
                    for (GameObject object : area.getNearSolidMobs()) {
                        if (object.getCollision() != null && object.getFloatHeight() < bladeHeight && Math.abs(getX() + xRadius - object.getX()) < xRadius +
                                object.getCollision().getWidthHalf() && Math.abs(getY() + yRadius - object.getY()) < yRadius + object.getCollision()
                                .getHeightHalf()) {
                            updateGrass = true;
                            break;
                        }
                    }
                }
            }
            if (updateGrass) {
                for (int i = 0; i < grasses.length; i++) {
                    if (grasses[i] != null) {
                        grasses[i].update();
                        grasses[i].setVisible(true);
                    }
                }
            } else {
                for (int i = 0; i < grasses.length; i++) {
                    if (grasses[i] != null) {
                        grasses[i].setVisible(false);
                        grasses[i].reset();
                    }
                }
            }
            if (!added) {
                for (int i = 0; i < grasses.length; i++) {
                    if (grasses[i] != null) {
                        map.addObject(grasses[i]);
                    }
                }
                added = true;
            }
        }
    }

    private void preRender() {
        if (!fbo.generated) {
            fbo.activate();
            glClearColor(0, 0.7f, 0, 0);
            glClear(GL_COLOR_BUFFER_BIT);
            Drawer.regularShader.translateDefault(xRadius + 2, -2 * yRadius + ySpacing + Display.getHeight());
            Drawer.regularShader.resetWorkingMatrix();
            if (corner >= 0) {
                preRenderCorner();
            } else if (curve > 0) {
                preRenderRound();
            } else {
                preRenderRectangle();
            }
            Drawer.regularShader.translateDefault(-xRadius - 2, 2 * yRadius - ySpacing - Display.getHeight());
            fbo.deactivate();
        }
    }

    private void preRenderRound() {
        int middle1 = yCount / 2, middle2 = yCount / 2, xCenter;
        if (yCount % 2 == 0) {
            middle1 -= 1;
        }
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                xCenter = 0;
                if (curve > 0 && xCount > 1) {
                    if (j == 0) {
                        xCenter = xCentering - (xBladesCount - (i <= middle1 ? (middle1 - i) : (i - middle2)) / curve) * Math.round((bladeWidth - 2) / 2f);
                        Drawer.regularShader.translateNoReset(xCenter, 0);
                    } else if (j == xCount - 1) {
                        xCenter = (xBladesCount - (i <= middle1 ? (middle1 - i) : (i - middle2)) / curve) * Math.round((bladeWidth - 2) / 2f) - xCentering;
                        Drawer.regularShader.translateNoReset(xCenter, 0);
                    }
                }
                grasses[i * xCount + j].renderStill();
                Drawer.regularShader.translateNoReset(grassWidth - xCenter, 0);
            }
            Drawer.regularShader.translateNoReset(-grassWidth * (xCount), ySpacing);
        }
    }

    private void preRenderRectangle() {
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                grasses[i * xCount + j].renderStill();
                Drawer.regularShader.translateNoReset(grassWidth, 0);
            }
            Drawer.regularShader.translateNoReset(-grassWidth * (xCount), ySpacing);
        }
    }

    private void preRenderCorner() {
        int middle1 = yCount / 2, middle2 = yCount / 2, modXBladesCount, xCenter;
        if (yCount % 2 == 0) {
            middle1 -= 1;
        }
        int xChange = (xCount * xBladesCount) / yCount;
        Drawer.regularShader.translateNoReset(-xCentering, 0);
        for (int i = 0; i < yCount; i++) {
            for (int j = 0; j < xCount; j++) {
                modXBladesCount = xBladesCount;
                if (corner == CORNER_DOWN_LEFT) {
                    modXBladesCount -= xChange * i - j * xBladesCount;
                    modXBladesCount += (yCount / 2 - (i <= middle1 ? (middle1 - i) : (i - middle2))) * curve;
                    if (modXBladesCount > xBladesCount) {
                        modXBladesCount = xBladesCount;
                    }
                    xCenter = xCentering + Math.round((xBladesCount - modXBladesCount) * bladeWidth * 0.375f);
                } else if (corner == CORNER_UP_RIGHT) {
                    modXBladesCount = xChange * (i + 1) - j * xBladesCount;
                    modXBladesCount += (yCount / 2 - (i <= middle1 ? (middle1 - i) : (i - middle2))) * curve;
                    if (modXBladesCount > xBladesCount) {
                        modXBladesCount = xBladesCount;
                    }
                    xCenter = xCentering - Math.round((xBladesCount - modXBladesCount) * bladeWidth * 0.375f);
                } else if (corner == CORNER_UP_LEFT) {
                    modXBladesCount -= xChange * (yCount - i - 1) - j * xBladesCount;
                    modXBladesCount += (yCount / 2 - (i <= middle1 ? (middle1 - i) : (i - middle2))) * curve;
                    if (modXBladesCount > xBladesCount) {
                        modXBladesCount = xBladesCount;
                    }
                    xCenter = xCentering + Math.round((xBladesCount - modXBladesCount) * bladeWidth * 0.375f);
                } else {
                    modXBladesCount = xChange * (yCount - i) - j * xBladesCount;
                    modXBladesCount += (yCount / 2 - (i <= middle1 ? (middle1 - i) : (i - middle2))) * curve;
                    if (modXBladesCount > xBladesCount) {
                        modXBladesCount = xBladesCount;
                    }
                    xCenter = xCentering - Math.round((xBladesCount - modXBladesCount) * bladeWidth * 0.375f);
                }
                Drawer.regularShader.translateNoReset(xCenter, 0);
                if (modXBladesCount > 0) {
                    grasses[i * xCount + j].renderStill();
                }
                Drawer.regularShader.translateNoReset(grassWidth - xCenter, 0);
            }
            Drawer.regularShader.translateNoReset(-grassWidth * (xCount), ySpacing);
        }
    }

    @Override
    public void render() {
        updateGrasses();
        if (!updateGrass) {
            preRender();
            Drawer.regularShader.translate(getX() + xCentering - xRadius - 2, (int) (getY() - fbo.getHeight() + 2 * yRadius - floatHeight));
            fbo.render();
        }
    }

    @Override
    public void renderShadowLit(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeShade(appearance, 1, getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadow(Figure figure) {
        if (appearance != null) {
            Drawer.drawShapeBlack(appearance, getX(), getY() - (int) floatHeight);
        }
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartShade(appearance, 1, getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
        if (appearance != null) {
            Drawer.drawShapePartBlack(appearance, getX(), getY() - (int) floatHeight, xStart, xEnd);
        }
    }

    @Override
    public int getXSpriteBegin(boolean... forCover) {
        return getX();
    }

    @Override
    public int getYSpriteBegin(boolean... forCover) {
        return getY() - bladeHeight;
    }

    public int getXSpriteEnd(boolean... forCover) {
        return getX() + fbo.getWidth();
    }

    public int getYSpriteEnd(boolean... forCover) {
        return getY() + fbo.getHeight();
    }

}
