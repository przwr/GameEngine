package sprites;

import engine.utilities.Drawer;
import engine.utilities.ErrorHandler;
import engine.utilities.Point;
import game.gameobject.entities.Player;
import game.place.Place;
import gamecontent.equipment.Cloth;
import org.lwjgl.opengl.Display;
import sprites.fbo.FrameBufferObject;
import sprites.fbo.RegularFrameBufferObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Wojtek
 */
public class ClothedAppearance implements Appearance {

    private final int xStart, yStart;
    private final int xDelta, yDelta;
    private final Animation upperBody, lowerBody;
    public int IDLE, WALK, RUN, SWORD, FISTS, ACROBATICS, BOW, SHIELD;
    private int framesPerDirection;
    private int xOffset, yOffset;
    private int width, height;
    private ClothCombo[] upperRenderList;
    private ClothCombo[] lowerRenderList;
    private ArrayList<byte[]> upperQueue;
    private ArrayList<byte[]> lowerQueue;
    private FrameBufferObject fbo;
    private boolean upToDate;

    public ClothedAppearance(Place place, int delayTime, String characterName, int width) {
        setClothParameters("characters/" + characterName);
        setRenderQueue("characters/" + characterName);
        Point[] renderPoints = place.getStartPointFromFile("characters/" + characterName);
        xStart = renderPoints[0].getX();
        yStart = renderPoints[0].getY();
        xDelta = renderPoints[1].getX();
        yDelta = renderPoints[1].getY();
        upperBody = Animation.createDirectionalAnimation(null, delayTime, framesPerDirection);
        lowerBody = Animation.createDirectionalAnimation(null, delayTime, framesPerDirection);
        fbo = new RegularFrameBufferObject(210, 256);
    }

    public void setClothes(Cloth head, Cloth torso, Cloth legs,
                           Cloth cap, Cloth hair, Cloth shirt, Cloth gloves,
                           Cloth pants, Cloth boots, Cloth sword, Cloth bow, Cloth shield) {
        lowerRenderList = new ClothCombo[]{
                new ClothCombo(legs.getLastPartNumber(), legs),
                new ClothCombo(boots.getFirstPartNumber(), boots),
                new ClothCombo(legs.getFirstPartNumber(), legs),
                new ClothCombo(boots.getLastPartNumber(), boots),
                new ClothCombo(pants.getFirstPartNumber(), pants),
                new ClothCombo(pants.getLastPartNumber(), pants)
        };
        upperRenderList = new ClothCombo[]{
                new ClothCombo(torso.getSecondPartNumber(), torso),
                new ClothCombo(gloves.getFirstPartNumber(), gloves),
                new ClothCombo(shirt.getSecondPartNumber(), shirt),
                new ClothCombo(torso.getFirstPartNumber(), torso),
                new ClothCombo(shirt.getFirstPartNumber(), shirt),
                new ClothCombo(head.getFirstPartNumber(), head),
                new ClothCombo(hair.getFirstPartNumber(), hair),
                new ClothCombo(cap.getFirstPartNumber(), cap),
                new ClothCombo(torso.getLastPartNumber(), torso),
                new ClothCombo(gloves.getLastPartNumber(), gloves),
                new ClothCombo(shirt.getLastPartNumber(), shirt),
                new ClothCombo(sword.getFirstPartNumber(), sword),
                new ClothCombo(bow.getFirstPartNumber(), bow),
                new ClothCombo(shield.getFirstPartNumber(), shield)
        };
        calculateDimensions();
    }

    private void calculateDimensions() {
        SpriteSheet[] ret = new SpriteSheet[upperRenderList.length];
        for (int i = 0; i < upperRenderList.length; i++) {
            ret[i] = upperRenderList[i].getSprite();
        }
        Point[] dims = SpriteSheet.getMergedDimensions(ret);
        width = dims[0].getX();
        height = dims[0].getY();
        xOffset = dims[1].getX();
        yOffset = dims[1].getY();
    }

    private void setRenderQueue(String folder) {
        try {
            FileReader fl = new FileReader(SpriteBase.fullFolderPath(folder) + "parts.txt");
            BufferedReader input = new BufferedReader(fl);
            String line;
            String[] data;
            char[] chars;
            byte[] upper;
            byte[] lower;
            char offset = 65;
            upperQueue = new ArrayList<>();
            lowerQueue = new ArrayList<>();
            while ((line = input.readLine()) != null) {
                data = line.split(":");
                chars = data[0].toCharArray();
                upper = new byte[chars.length];
                for (int j = 0; j < chars.length; j++) {
                    upper[j] = (byte) (chars[j] - offset);
                }
                chars = data[1].toCharArray();
                lower = new byte[chars.length];
                for (int k = 0; k < chars.length; k++) {
                    lower[k] = (byte) (chars[k] - offset);
                }
                upperQueue.add(upper);
                lowerQueue.add(lower);
            }
            input.close();
            fl.close();
        } catch (IOException e) {
            ErrorHandler.error("File " + folder + File.pathSeparator + "parts.txt not found!\n" + e.getMessage());
        }
    }

    private void setClothParameters(String folder) {
        try {
            FileReader fl = new FileReader(SpriteBase.fullFolderPath(folder) + "intervals.txt");
            BufferedReader input = new BufferedReader(fl);
            String line;
            String[] data;
            while ((line = input.readLine()) != null) {
                data = line.split(":");
                switch (data[0]) {
                    case "idle":
                        IDLE = Integer.parseInt(data[1]);
                        break;
                    case "walk":
                        WALK = Integer.parseInt(data[1]);
                        break;
                    case "run":
                        RUN = Integer.parseInt(data[1]);
                        break;
                    case "sword":
                        SWORD = Integer.parseInt(data[1]);
                        break;
                    case "fists":
                        FISTS = Integer.parseInt(data[1]);
                        break;
                    case "acro":
                        ACROBATICS = Integer.parseInt(data[1]);
                        break;
                    case "bow":
                        BOW = Integer.parseInt(data[1]);
                        break;
                    case "shield":
                        SHIELD = Integer.parseInt(data[1]);
                        break;
                    default:
                        framesPerDirection = Integer.parseInt(data[0]);
                }
            }
            input.close();
            fl.close();
        } catch (IOException e) {
            ErrorHandler.error("File " + folder + File.pathSeparator + "intervals.txt not found!\n" + e.getMessage());
        }
    }

    private void synchronize() {
        lowerBody.setCurrentFrame(upperBody.getCurrentFrameIndex());
    }

    public Animation getUpperBody() {
        return upperBody;
    }

    public Animation getLowerBody() {
        return lowerBody;
    }

    public void setFramesPerDirection(int frames) {
        upperBody.setFramesPerDirection(frames);
        lowerBody.setFramesPerDirection(frames);
    }

    public void animateSingleInDirection(int direction, int interval, int index) {
        upperBody.animateSingleInDirection(direction, interval + index);
        lowerBody.animateSingleInDirection(direction, interval + index);
    }

    public void animateWhole() {
        upperBody.animateWhole();
        lowerBody.animateWhole();
        synchronize();
    }

    public void animateIntervalInDirection(int direction, int interval, int start, int end) {
        upperBody.animateIntervalInDirection(direction, interval + start, interval + end);
        lowerBody.animateIntervalInDirection(direction, interval + start, interval + end);
        synchronize();
    }

    public void changeDirection(int direction) {
        upperBody.changeDirection(direction);
        lowerBody.changeDirection(direction);
        synchronize();
    }

    public void reverseAnimation() {
        upperBody.reverseAnimation();
        lowerBody.reverseAnimation();
    }

    public void setReversed(boolean reversed) {
        upperBody.setReversed(reversed);
        lowerBody.setReversed(reversed);
    }

    public void animateIntervalInDirectionOnce(int direction, int interval, int start, int end) {
        upperBody.animateIntervalInDirectionOnce(direction, interval + start, interval + end);
        lowerBody.animateIntervalInDirectionOnce(direction, interval + start, interval + end);
        synchronize();
    }

    public void animateIntervalInDirectionFluctuating(int direction, int interval, int start, int end) {
        upperBody.animateIntervalInDirectionFluctuating(direction, interval + start, interval + end);
        lowerBody.animateIntervalInDirectionFluctuating(direction, interval + start, interval + end);
        synchronize();
    }

    public void setFluctuating(boolean fluctuate) {
        upperBody.setFluctuating(fluctuate);
        lowerBody.setFluctuating(fluctuate);
    }

    public void setFPS(int fps) {
        upperBody.setFPS(fps);
        lowerBody.setFPS(fps);
    }

    public boolean isAnimating() {
        if (upperBody.isAnimating() == lowerBody.isAnimating()) {
            return upperBody.isAnimating();
        } else {
            return false;
        }
    }

    public void setAnimate(boolean animate) {
        upperBody.setAnimate(animate);
        lowerBody.setAnimate(animate);
    }

    @Override
    public boolean bindCheck() {
        return fbo.bindCheck();
    }

    @Override
    public void render() {
        Drawer.translate(-xStart + xDelta, -yStart + yDelta);
        int upperFrame = upperBody.getCurrentFrameIndex();
        int lowerFrame = lowerBody.getCurrentFrameIndex();
        for (byte i : upperQueue.get(upperFrame)) {
            if (!isThisLowerPlacement(i)) {
                if (upperRenderList[i].cloth.isWearing() && upperRenderList[i].getSprite() != null) {
                    upperRenderList[i].getSprite().renderPiece(upperFrame);
                }
            } else {
                for (byte j : lowerQueue.get(lowerFrame)) {
                    if (lowerRenderList[j].cloth.isWearing() && lowerRenderList[j].getSprite() != null) {
                        lowerRenderList[j].getSprite().renderPiece(lowerFrame);
                    }
                }
            }
        }
    }

    private boolean isThisLowerPlacement(byte i) {
        return i == ('0' - 'A');
    }

    @Override
    public void updateFrame() {
        upperBody.updateFrame();
        lowerBody.updateFrame();
    }

    @Override
    public int getCurrentFrameIndex() {
        if (upperBody.getCurrentFrameIndex() == lowerBody.getCurrentFrameIndex()) {
            return upperBody.getCurrentFrameIndex();
        } else {
            return -1;
        }
    }

    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public int getXStart() {
        return -xStart + xDelta;
    }

    @Override
    public int getYStart() {
        return -yStart + yDelta;
    }

    @Override
    public int getActualWidth() {
        return width;
    }

    @Override
    public int getActualHeight() {
        return height;
    }

    @Override
    public int getXOffset() {
        return xOffset / 2;
    }

    @Override
    public int getYOffset() {
        return yOffset / 2;
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        glTranslatef(-fbo.getWidth() / 2, -fbo.getHeight() / 2, 0);
        if (partXStart > partXEnd) {
            int temp = partXEnd;
            partXEnd = partXStart;
            partXStart = temp;
        }
        int startDelta = partXStart;
        int endDelta = fbo.getWidth() - partXEnd;
        if (startDelta > endDelta) {
            fbo.renderPart(partXStart - xOffset / 2, partXEnd - xOffset / 2);
        } else {
            fbo.renderPart(partXStart + xOffset / 2, partXEnd + xOffset / 2);
        }
    }

    @Override
    public void renderShadow(float color) {
        glTranslatef(-fbo.getWidth() / 2, -fbo.getHeight() / 2, 0);
        fbo.renderShadow(color);
        glTranslatef(fbo.getWidth() / 2, fbo.getHeight() / 2, 0);
    }

    @Override
    public void renderShadowPart(int partXStart, int partXEnd, float color) {
        glTranslatef(-fbo.getWidth() / 2, -fbo.getHeight() / 2, 0);
        if (partXStart > partXEnd) {
            int temp = partXEnd;
            partXEnd = partXStart;
            partXStart = temp;
        }
        int startDelta = partXStart;
        int endDelta = fbo.getWidth() - partXEnd;
        if (startDelta > endDelta) {
            fbo.renderShadowPart(partXStart - xOffset / 2, partXEnd - xOffset / 2, color);
        } else {
            fbo.renderShadowPart(partXStart + xOffset / 2, partXEnd + xOffset / 2, color);
        }
        glTranslatef(fbo.getWidth() / 2, fbo.getHeight() / 2, 0);
    }


    @Override
    public void updateTexture(Player owner) {
        fbo.activate();
        glPushMatrix();
        glClear(GL_COLOR_BUFFER_BIT);
        glColor3f(1, 1, 1);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        glTranslatef(fbo.getWidth() / 2, -fbo.getHeight() / 2 + Display.getHeight(), 0);
        render();
        glPopMatrix();
        fbo.deactivate();
    }

    public boolean isUpToDate() {
        if (!upToDate) {
            for (int i = 0; i < upperRenderList.length; i++) {
                if (upperRenderList[i].getSprite() != null && upperRenderList[i].getSprite().getTextureID() == 0) {
                    return false;
                }
            }
            for (int i = 0; i < lowerRenderList.length; i++) {
                if (lowerRenderList[i].getSprite() != null && lowerRenderList[i].getSprite().getTextureID() == 0) {
                    return false;
                }
            }
            if (upperBody.isUpToDate() && lowerBody.isUpToDate()) {
                upToDate = true;
                return true;
            } else {
                return false;
            }
        } else {
            return true;
        }
    }

    private class ClothCombo {

        int which;
        Cloth cloth;

        public ClothCombo(int which, Cloth cloth) {
            this.which = which;
            this.cloth = cloth;
        }

        SpriteSheet getSprite() {
            return which >= 0 ? cloth.getPart(which) : null;
        }

    }
}
