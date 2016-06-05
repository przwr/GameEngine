package game.gameobject.items;

import engine.utilities.Drawer;
import game.place.Place;
import org.newdawn.slick.Color;

/**
 * Created by przemek on 02.09.15.
 */
public class Weapon extends Item {

    public static byte UNIVERSAL = 0, SWORD = 1, BOW = 2;

    private final byte type;
    private float modifier = 1;
    private float knockback = 1;


    public Weapon(int x, int y, String name, Place place, float weight, String spriteName, byte type) {
        super(x, y, name, place, weight, spriteName, place != null ? place.getNextItemID() : -1);
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    @Override
    public void renderIcon() {
        Drawer.setColorStatic(Color.gray);
        Drawer.drawRectangle(-1, -10, 2, 20);
        Drawer.refreshColor();
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
