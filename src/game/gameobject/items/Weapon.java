package game.gameobject.items;

import game.place.Place;

/**
 * Created by przemek on 02.09.15.
 */
public class Weapon extends Item {

    public static byte UNIVERSAL = 0, SWORD = 1, BOW = 2;

    private final byte type;
    private float modifier = 1;
    private float knockback = 1;


    public Weapon(int x, int y, String name, Place place, float weight, String spriteName, short itemID, byte type) {
        super(x, y, name, place, weight, spriteName, itemID);
        this.type = type;
    }

    public byte getType() {
        return type;
    }


    public float getModifier() {
        return modifier;
    }

    public void setModifier(float modifier) {
        this.modifier = modifier;
    }

    public float getKnockback() {
        return knockback;
    }

    public void setKnockback(float knockback) {
        this.knockback = knockback;
    }

    //TODO wygląd broni i statystyki

}
