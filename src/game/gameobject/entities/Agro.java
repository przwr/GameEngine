package game.gameobject.entities;

import game.gameobject.GameObject;

/**
 * Created by przemek on 02.11.15.
 */
public class Agro {
    GameObject agresor;
    int value;

    public Agro() {
    }

    public Agro(GameObject agresor, int value) {
        this.agresor = agresor;
        this.value = value;
    }

    public void set(GameObject agresor, int value) {
        this.agresor = agresor;
        this.value = value;
    }

    public void addValue(int value) {
        this.value += value;
    }

    public int getValue() {
        return value;
    }
}
