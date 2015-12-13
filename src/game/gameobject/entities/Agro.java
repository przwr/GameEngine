package game.gameobject.entities;

import game.gameobject.GameObject;

/**
 * Created by przemek on 02.11.15.
 */
public class Agro {
    GameObject agresor;
    int hurtsOwner, hurtedByOwner;

    public Agro() {
    }

    public Agro(GameObject agresor) {
        this.agresor = agresor;
    }

    public Agro(GameObject agresor, int hurtsOwner) {
        this.agresor = agresor;
        this.hurtsOwner = hurtsOwner;
    }

    public void set(GameObject agresor, int hurtsOwner) {
        this.agresor = agresor;
        this.hurtsOwner = hurtsOwner;
    }

    public void addHurtsOwner(int hurtsOwner) {
        this.hurtsOwner += hurtsOwner;
    }

    public void clearHurtedByOwner() {
        this.hurtedByOwner = 0;
    }

    public void addHurtedByOwner(int hurtedByOwner) {
        this.hurtedByOwner += hurtedByOwner;
    }

    public int getHurtsOwner() {
        return hurtsOwner;
    }

    public int getHurtedByOwner() {
        return hurtedByOwner;
    }
}
