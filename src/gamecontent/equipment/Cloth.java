/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package gamecontent.equipment;

import game.place.Place;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import sprites.SpriteSheet;

/**
 *
 * @author Wojtek
 */
public class Cloth {

    ArrayList<SpriteSheet> list;

    public Cloth(String name, Place place) throws FileNotFoundException {
        if (new File("res/textures/cloth/" + name + ".spr").exists()) {
            list = new ArrayList<>(1);
            list.add(place.getSpriteSheet("cloth/" + name));
        } else {
            list = new ArrayList<>(2);
            int i = 0;
            while (new File("res/textures/cloth/" + name + i + ".spr").exists()) {
                list.add(place.getSpriteSheet("cloth/" + name + i));
                i++;
            }
        }
        if (list.isEmpty()) {
            throw new FileNotFoundException(name);
        }
    }
/*
    public void addClothes(ArrayList<ClothPart> otherList, int addPriority) {
        for (ClothPart cp : list) {
            cp.priority += addPriority;
        }
        otherList.addAll(list);
    }
    */
        
    public SpriteSheet getLeftPart() {
        return list.get(0);
    }
    
    public SpriteSheet getRightPart() {
        return list.get(list.size() - 1);
    }
    
    public SpriteSheet getCentralPart() {
        if (list.size() > 2)
            return list.get(1);
        return null;
    }
/*
    public static void sortClothes(ArrayList<ClothPart> list) {
        int i, j, newValue;
        ClothPart cloth;
        for (i = 1; i < list.size(); i++) {
            cloth = list.get(i);
            newValue = cloth.priority;
            j = i;
            while (j > 0 && list.get(j - 1).priority > newValue) {
                list.set(j, list.get(j - 1));
                j--;
            }
            list.set(j, cloth);
        }
    }

    public class ClothPart {

        int priority;
        SpriteSheet sprite;

        protected ClothPart(int priority, SpriteSheet sprite) {
            this.priority = priority;
            this.sprite = sprite;
        }

        public int getPriority() {
            return priority;
        }

        public SpriteSheet getSprite() {
            return sprite;
        }
    }*/
}
