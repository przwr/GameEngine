/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent;

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
import static org.lwjgl.opengl.GL11.glTranslatef;
import sprites.Appearance;
import sprites.SpriteBase;

/**
 *
 * @author Wojtek
 */
public class ClothedAppearance implements Appearance {

    public int IDLE, WALK, RUN, SWORD, FISTS, ACROBATICS, BOW, SHIELD;

    private int framesPerDirection, frame;
    private int xStart, yStart;
    private int width, height;
    private int xCentral, yCentral;
    private int xDelta, yDelta;

    private Cloth head = Cloth.nullCloth;
    private Cloth torso = Cloth.nullCloth;
    private Cloth legs = Cloth.nullCloth;
    private Cloth nudeTorso = Cloth.nullCloth;
    private Cloth nudeLegs = Cloth.nullCloth;

    private Cloth cap = Cloth.nullCloth;
    private Cloth hair = Cloth.nullCloth;
    private Cloth shirt = Cloth.nullCloth;
    private Cloth gloves = Cloth.nullCloth;
    private Cloth pants = Cloth.nullCloth;
    private Cloth boots = Cloth.nullCloth;

    private Cloth weapon = Cloth.nullCloth;

    public ClothedAppearance(Place place, String characterName) {
        setClothParameters("characters/" + characterName);
        Point[] renderPoints = place.getStartPointFromFile("characters/" + characterName);
        xCentral = renderPoints[0].getX();
        yCentral = renderPoints[0].getY();
        xDelta = renderPoints[1].getX();
        yDelta = renderPoints[1].getY();
    }

    public void setClothes(Cloth head, Cloth torso, Cloth legs,
            Cloth nudeTorso, Cloth nudeLegs, Cloth cap,
            Cloth hair, Cloth shirt, Cloth gloves,
            Cloth pants, Cloth boots, Cloth weapon) {
        this.head = head;
        this.torso = torso;
        this.legs = legs;
        this.nudeTorso = nudeTorso;
        this.nudeLegs = nudeLegs;
        this.cap = cap;
        this.hair = hair;
        this.shirt = shirt;
        this.gloves = gloves;
        this.pants = pants;
        this.boots = boots;
        this.weapon = weapon;
        calculateDimensions();
    }

    private void calculateDimensions() {
        Point[] dims = Cloth.getMergedDimensions(
                head, torso, legs, hair,
                cap, shirt, gloves, pants, boots, weapon);
        int tempx = dims[0].getX();
        int tempy = dims[0].getY();
        width = Methods.roundUpToBinaryNumber(dims[0].getX());
        height = Methods.roundUpToBinaryNumber(dims[0].getY());
        xStart = xCentral - (dims[1].getX() - (dims[0].getX() - tempx) / 2);
        yStart = yCentral - (dims[1].getY() - (dims[0].getY() - tempy) / 2);
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

    public int getFramesPerDirection() {
        return framesPerDirection;
    }

    public void setFramesPerDirection(int frames) {
        framesPerDirection = frames;
    }
    
    public void setFrame(int frame) {
        this.frame = frame;
    }

    private void renderClothedUpperBody() {
        Cloth.renderTorso(shirt.isWearing() ? nudeTorso : torso, shirt, gloves, weapon, frame / framesPerDirection, frame);
        Cloth.renderHead(head, hair, cap, frame / framesPerDirection, frame);
    }

    private void renderClothedLowerBody() {
        Cloth.renderLegs(pants.isWearing() ? nudeLegs : legs, boots, pants, frame / framesPerDirection, frame);
    }

    @Override
    public void bindCheck() {
        //Nothing to do..... <('^'<)
    }

    @Override
    public void render() {
        glTranslatef(-xCentral + xDelta, -yCentral + xDelta, 0);
        renderClothedLowerBody();
        renderClothedUpperBody();
    }

    @Override
    public void renderMirrored() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void renderPart(int partXStart, int partXEnd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void renderPartMirrored(int partXStart, int partXEnd) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateTexture(Player owner) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void updateFrame() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getCurrentFrameIndex() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getWidth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getXStart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getYStart() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getActualWidth() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getActualHeight() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getXOffset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public int getYOffset() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
