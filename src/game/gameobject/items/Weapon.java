package game.gameobject.items;

/**
 * Created by przemek on 02.09.15.
 */
public class Weapon {

    public static byte UNIVERSAL = 0, SWORD = 1;

    private byte type;
    private String name;


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

    //TODO wygląd broni i statystyki

}
