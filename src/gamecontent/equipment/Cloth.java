/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.equipment;

import engine.Point;
import game.place.Place;
import sprites.SpriteSheet;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;

/**
 * @author Wojtek
 */
public class Cloth {

    private final ArrayList<SpriteSheet> list;

    public Cloth(String cloth, String character, Place place) throws FileNotFoundException {
        list = new ArrayList<>(2);
        int i = 0;
        while (new File("res/textures/cloth/" + character + "/" + cloth + (i > 0 ? i : "") + ".spr").exists()) {
            list.add(place.getSpriteSheet(cloth + (i > 0 ? i : ""), "cloth/" + character));
            i++;
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(cloth);
        }
    }

    public SpriteSheet getSecondPart() {
        if (list.size() > 2) {
            return list.get(1);
        }
        return null;
    }

    public SpriteSheet getLastPart() {
        return list.get(list.size() - 1);
    }

    public SpriteSheet getFirstPart() {
        return list.get(0);
    }

    public static Point[] getMergedDimensions(Cloth... list) {
        int length = 0;
        for (Cloth c : list) {
            if (c != null) {
                length += c.list.size();
            }
        }
        SpriteSheet[] slist = new SpriteSheet[length];
        int i = 0;
        for (Cloth c : list) {
            if (c != null) {
                for (SpriteSheet s : c.list) {
                    slist[i] = s;
                    i++;
                }
            }
        }
        return SpriteSheet.getMergedDimensions(slist);
    }
}
