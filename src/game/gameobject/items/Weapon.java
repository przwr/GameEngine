package game.gameobject.items;

/**
 * Created by przemek on 02.09.15.
 */
public class Weapon {

    public static byte UNIVERSAL = 0, SWORD = 1, BOW = 2;

    private final byte type;
    private String name;
    private float modifier = 1;
    private float knockback = 1;


    public Weapon(String name, byte type) {
        this.name = name;
        this.type = type;
    }

    public byte getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
