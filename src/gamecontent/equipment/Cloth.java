/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.equipment;

import engine.utilities.Point;
import game.place.Place;
import sprites.SpriteSheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Wojtek
 */
public class Cloth {

    public final static String BODY_TYPE = "body", CLOTH_TYPE = "cloth";
    public final static Cloth nullCloth = new NullCloth();
    private final ArrayList<SpriteSheet> list;
    private boolean wearing;

    private Cloth() {
        list = null;
    }

    public Cloth(String cloth, String type, String character, Place place) throws FileNotFoundException {
        list = new ArrayList<>(2);
        int i = 0;
        while (new File("res/textures/characters/" + character + "/" + type + "/" + cloth + (i > 0 ? i : "") + ".spr").exists()) {
            list.add(place.getSpriteSheet(cloth + (i > 0 ? i : ""), "characters/" + character + "/" + type));
            i++;
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(cloth);
        }
        wearing = true;
    }

    public static Point[] getMergedDimensions(Cloth... list) {
        int length = 0;
        for (Cloth c : list) {
            if (!(c instanceof NullCloth)) {
                length += c.list.size();
            }
        }
        SpriteSheet[] slist = new SpriteSheet[length];
        int i = 0;
        for (Cloth c : list) {
            if (!(c instanceof NullCloth)) {
                for (SpriteSheet s : c.list) {
                    slist[i] = s;
                    i++;
                }
            }
        }
        return SpriteSheet.getMergedDimensions(slist);
    }

    public boolean isWearing() {
        return wearing;
    }

    public void setWearing(boolean wearing) {
        this.wearing = wearing;
    }

    public void renderSecondPart(int frame) {
        if (wearing && list.size() > 2) {
            list.get(1).renderPieceAndReturn(frame);
        }
    }

    public void renderLastPart(int frame) {
        if (wearing) {
            list.get(list.size() - 1).renderPieceAndReturn(frame);
        }
    }

    public void renderFirstPart(int frame) {
        if (wearing) {
            list.get(0).renderPieceAndReturn(frame);
        }
    }

    public static void renderHead(Cloth head, Cloth hair, Cloth cap, int direction8Way, int frame) {
        head.renderFirstPart(frame);
        hair.renderFirstPart(frame);
        cap.renderFirstPart(frame);
    }

    public static void renderTorso(Cloth torso, Cloth shirtIII, Cloth glovesIIIIII, Cloth weapon, int direction8Way, int frame) {
        switch (direction8Way) {
            case 7:
            case 0:
            case 1:
                torso.renderSecondPart(frame);
                glovesIIIIII.renderFirstPart(frame);
                shirtIII.renderSecondPart(frame);
                torso.renderFirstPart(frame);
                shirtIII.renderFirstPart(frame);
                torso.renderLastPart(frame);
                glovesIIIIII.renderLastPart(frame);
                shirtIII.renderLastPart(frame);
                break;
            case 2:
                torso.renderSecondPart(frame);
                torso.renderLastPart(frame);
                torso.renderFirstPart(frame);
                glovesIIIIII.renderFirstPart(frame);
                shirtIII.renderSecondPart(frame);
                glovesIIIIII.renderLastPart(frame);
                shirtIII.renderLastPart(frame);
                shirtIII.renderFirstPart(frame);
                break;
            case 3:
            case 4:
            case 5:
                torso.renderLastPart(frame);
                glovesIIIIII.renderLastPart(frame);
                shirtIII.renderLastPart(frame);
                torso.renderFirstPart(frame);
                shirtIII.renderFirstPart(frame);
                torso.renderSecondPart(frame);
                glovesIIIIII.renderFirstPart(frame);
                shirtIII.renderSecondPart(frame);
                break;
            case 6:
                torso.renderFirstPart(frame);
                torso.renderSecondPart(frame);
                torso.renderLastPart(frame);
                shirtIII.renderFirstPart(frame);
                glovesIIIIII.renderFirstPart(frame);
                shirtIII.renderSecondPart(frame);
                glovesIIIIII.renderLastPart(frame);
                shirtIII.renderLastPart(frame);
                break;
        }
    }

    public static void renderLegs(Cloth legs, Cloth boots, Cloth pants, int direction8Way, int frame) {
        switch (direction8Way) {
            case 7:
            case 0:
            case 1:
            case 2:
                legs.renderFirstPart(frame);
                boots.renderFirstPart(frame);
                legs.renderLastPart(frame);
                boots.renderLastPart(frame);
                pants.renderFirstPart(frame);
                pants.renderLastPart(frame);
                break;
            case 3:
            case 4:
            case 5:
            case 6:
                legs.renderLastPart(frame);
                boots.renderLastPart(frame);
                legs.renderFirstPart(frame);
                boots.renderFirstPart(frame);
                pants.renderLastPart(frame);
                pants.renderFirstPart(frame);
                break;
        }
    }

    static class NullCloth extends Cloth {

        @Override
        public void renderSecondPart(int frame) {
        }

        @Override
        public void renderLastPart(int frame) {
        }

        @Override
        public void renderFirstPart(int frame) {
        }

    }
}
