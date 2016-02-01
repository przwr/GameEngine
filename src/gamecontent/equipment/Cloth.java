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

    public final static String BODY_TYPE = "body", CLOTH_TYPE = "cloth", WEAPON_TYPE = "weapons";
    public final static Cloth nullCloth = new NullCloth();
    private final ArrayList<SpriteSheet> list;
    private boolean wearing, hasLast, hasSecond;

    private Cloth() {
        list = null;
    }

    public Cloth(String cloth, String type, String character, Place place) throws FileNotFoundException {
        list = new ArrayList<>(2);
        int i = 0;
        while (new File("res/textures/characters/" + character + "/" + type + "/" + cloth + (i > 0 ? i : "") + ".spr").exists()) {
            SpriteSheet spriteSheet = place.getSpriteSheet(cloth + (i > 0 ? i : ""), "characters/" + character + "/" + type);
            spriteSheet.setUnload(false);
            list.add(spriteSheet);
            i++;
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(cloth);
        } else {
            hasLast = list.size() > 1;
            hasSecond = list.size() > 2;
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

    public SpriteSheet getFirstPart() {
        return list.get(0);
    }

    public SpriteSheet getSecondPart() {
        return hasSecond ? list.get(1) : null;
    }

    public SpriteSheet getLastPart() {
        return hasLast ? list.get(list.size() - 1) : null;
    }

    public boolean isNull() {
        return false;
    }

    static class NullCloth extends Cloth {

        @Override
        public boolean isNull() {
            return true;
        }

        @Override
        public boolean isWearing() {
            return false;
        }

    }
}
