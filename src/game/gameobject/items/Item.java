package game.gameobject.items;

import collision.Figure;
import game.gameobject.GameObject;

/**
 * Created by Domi on 2016-05-02.
 */
public class Item extends GameObject {
    private float weight;


    public Item(String name, float weight) {
        this.name = name;
        this.weight = weight;
    }

    @Override
    public void render() {
    }

    @Override
    public void renderShadowLit(Figure figure) {
    }

    @Override
    public void renderShadowLit(int xStart, int xEnd) {
    }

    @Override
    public void renderShadow(Figure figure) {
    }

    @Override
    public void renderShadow(int xStart, int xEnd) {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}