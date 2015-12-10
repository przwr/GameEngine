/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sprites;

import engine.utilities.Drawer;
import engine.utilities.ErrorHandler;
import engine.utilities.Methods;
import engine.utilities.Point;
import game.gameobject.entities.Player;
import game.place.Place;
import gamecontent.equipment.Cloth;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Wojtek
 */
public class ClothedAppearance implements Appearance {

    public int IDLE, WALK, RUN, SWORD, FISTS, ACROBATICS, BOW, SHIELD;

    private int framesPerDirection, frame;
    private int xOffset, yOffset;
    private int width, height;
    private final int xStart, yStart;
    private final int xDelta, yDelta;

    private final Animation upperBody, lowerBody;

    private SpriteSheet[] upperRenderList;
    private SpriteSheet[] lowerRenderList;
    private ArrayList<byte[]> upperQueue;
    private ArrayList<byte[]> lowerQueue;

    public ClothedAppearance(Place place, int delayTime, String characterName) {
        setClothParameters("characters/" + characterName);
        setRenderQueue("characters/" + characterName);
        Point[] renderPoints = place.getStartPointFromFile("characters/" + characterName);
        xStart = renderPoints[0].getX();
        yStart = renderPoints[0].getY();
        xDelta = renderPoints[1].getX();
        yDelta = renderPoints[1].getY();
        upperBody = Animation.createDirectionalAnimation(null, delayTime, framesPerDirection);
        lowerBody = Animation.createDirectionalAnimation(null, delayTime, framesPerDirection);
    }

    public void setClothes(Cloth head, Cloth torso, Cloth legs,
            Cloth cap, Cloth hair, Cloth shirt, Cloth gloves,
            Cloth pants, Cloth boots, Cloth weapon) {
        lowerRenderList = new SpriteSheet[]{
            legs.getFirstPart(),
            boots.getFirstPart(),
            legs.getLastPart(),
            boots.getLastPart(),
            pants.getFirstPart(),
            pants.getLastPart()
        };
        upperRenderList = new SpriteSheet[]{
            torso.getSecondPart(),
            gloves.getFirstPart(),
            shirt.getSecondPart(),
            torso.getFirstPart(),
            shirt.getFirstPart(),
            head.getFirstPart(),
            hair.getFirstPart(),
            cap.getFirstPart(),
            torso.getLastPart(),
            gloves.getLastPart(),
            shirt.getLastPart(),
            weapon.getFirstPart()
        };
        calculateDimensions();
    }

    private void calculateDimensions() {
        Point[] dims = SpriteSheet.getMergedDimensions(upperRenderList);
        width = dims[0].getX();
        height = dims[0].getY();
        xOffset = dims[1].getX();
        yOffset = dims[1].getY();
    }

    private void setRenderQueue(String folder) {
        try (BufferedReader input = new BufferedReader(
                new FileReader(SpriteBase.fullFolderPath(folder) + "parts.txt"))) {
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
        } catch (IOException e) {
            ErrorHandler.error("File " + folder + File.pathSeparator + "parts.txt not found!\n" + e.getMessage());
        }
    }

    private void setClothParameters(String folder) {
        try (BufferedReader input = new BufferedReader(
                new FileReader(SpriteBase.fullFolderPath(folder) + "intervals.txt"))) {
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

    private void renderClothedUpperBody() {
        frame = upperBody.getCurrentFrameIndex();
        for (byte i : upperQueue.get(frame)) {
            upperRenderList[i].renderPieceAndReturn(frame);
        }
    }

    private void renderClothedLowerBody() {
        frame = lowerBody.getCurrentFrameIndex();
        for (byte i : lowerQueue.get(frame)) {
            lowerRenderList[i].renderPieceAndReturn(frame);
        }
    }

    public void setFPS(int fps) {
        upperBody.setFPS(fps);
        lowerBody.setFPS(fps);
    }

    public void setAnimate(boolean animate) {
        upperBody.setAnimate(animate);
        lowerBody.setAnimate(animate);
    }

    @Override
    public void bindCheck() {
        //Nothing to do..... <('^'<)
    }

    @Override
    public void render() {
        Drawer.translate(-xStart + xDelta, -yStart + yDelta);
        renderClothedLowerBody();
        renderClothedUpperBody();
    }

    @Override
    public void updateFrame() {
        upperBody.updateFrame();
        lowerBody.updateFrame();
    }

    @Override
    public int getCurrentFrameIndex() {
        throw new UnsupportedOperationException("You have no idea WAT U DOOIN'");
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
        return xOffset;
    }

    @Override
    public int getYOffset() {
        return yOffset;
    }

    @Override
    public void renderMirrored() {
        throw new UnsupportedOperationException("You have no idea WAT U DOOIN'");
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        throw new UnsupportedOperationException("You have no idea WAT U DOOIN'");
    }

    @Override
    public void renderPartMirrored(int partXStart, int partXEnd) {
        throw new UnsupportedOperationException("You have no idea WAT U DOOIN'");
    }

    @Override
    public void updateTexture(Player owner) {
        throw new UnsupportedOperationException("You have no idea WAT U DOOIN'");
    }
}
