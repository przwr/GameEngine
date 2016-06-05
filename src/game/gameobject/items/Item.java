package game.gameobject.items;

import game.gameobject.GameObject;
import game.place.Place;

/**
 * Created by Domi on 2016-05-02.
 */
public class Item extends GameObject {


    public static Item EMPTY = new EmptyItem();
    public short itemID;
    private float weight;


    public Item(int x, int y, String name, Place place, float weight, String spriteName, short itemID) {
        super.initialize(name, x, y);
        this.weight = weight;
        this.place = place;
        this.itemID = itemID;
        this.setCanInteract(true);
        if (spriteName != null) {
            this.appearance = place.getSprite(spriteName, "entities/mobs", false, true);
        }
    }

    public void renderIcon() {
        if (appearance != null) {
            appearance.render();
        }
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}