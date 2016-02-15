/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package game.place.map;

import engine.utilities.RandomGenerator;
import game.gameobject.GameObject;
import game.place.Place;
import gamecontent.environment.Bush;
import gamecontent.environment.Tree;

/**
 *
 * @author Wojtek
 */
public class MapObjectContainer {

    private int x, y;
    private String[] data;
    private byte type;

    public static final byte TREE = 0, BUSH = 1;

    private static final String[] names = new String[]{"Tree", "Bush"};

    public MapObjectContainer(String[] data) {
        if (data[0].equals("o")) {
            this.data = data;
            x = Integer.parseInt(data[2]) * Place.tileSize;
            y = Integer.parseInt(data[3]) * Place.tileSize;
            type = -1;
            for (int i = 0; i < names.length; i++) {
                if (data[1].equals(names[i])) {
                    type = (byte) i;
                    break;
                }
            }
            if (type == -1) {
                throw new RuntimeException("Unknown object: " + data[1]);
            }
        }
    }

    public static String[] getNames() {
        return names;
    }

    public GameObject generateObject(int x, int y, RandomGenerator rand) {
        return generate(this.x + x, this.y + y, rand, type);
    }

    public static GameObject generate(int x, int y, RandomGenerator rand, byte type) {
        int dx = 0, dy = 0;
        if (rand != null) {
            dx = rand.random(Place.tileSize - 1);
            dy = rand.random(Place.tileSize - 1);
        }
        switch (type) {
            case TREE:
                return Tree.create(x + dx, y + dy);
            case BUSH:
                return new Bush(x + dx, y + dy);
        }
        return null;
    }
}
