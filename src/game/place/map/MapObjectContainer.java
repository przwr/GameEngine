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
import gamecontent.environment.GrassClump;
import gamecontent.environment.Tree;

/**
 * @author Wojtek
 */
public class MapObjectContainer {

    public static final byte TREE = 0, BUSH = 1, GRASS = 2;
    private static final String[] names = new String[]{"Tree", "Bush", "Grass"};
    private int x, y;
    private String[] data;
    private byte type;

    public MapObjectContainer(String[] data) {
        if (data[0].equals("o")) {
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
            if (data.length > 4) {
                String[] tmp = new String[data.length - 4];
                System.arraycopy(data, 4, tmp, 0, tmp.length);
                this.data = tmp;
            } else {
                this.data = null;
            }
        }
    }

    public static String[] getNames() {
        return names;
    }

    public static GameObject generate(int x, int y, RandomGenerator rand, byte type, String... data) {
        int dx = 0, dy = 0;
        if (rand != null) {
            dx = rand.random(Place.tileSize - 1);
            dy = rand.random(Place.tileSize - 1);
        }
        GameObject ret = null;
        switch (type) {
            case TREE:
//                ret = Tree.createBackground(x + dx, y + dy, 32, 200, 0.8f);
                ret = Tree.create(x + dx, y + dy, 32, 200, 0.8f);
                break;
            case BUSH:
                ret = new Bush(x + dx, y + dy);
                break;
            case GRASS:
                if (data != null && data.length != 0) {
                    int subtype = Integer.parseInt(data[0]);
                    if (subtype <= 3) {
                        ret = GrassClump.createCorner(x, y, 1, 8, 11, 2, 8, 32, subtype);
                    } else {
                        ret = GrassClump.createRound(x, y, 1, 8, 11, 2, 8, 32);
                    }
                } else {
                    ret = GrassClump.createRectangle(x, y, 1, 8, 11, 2, 8, 32);
                }
                break;
        }
        if (ret != null) {
            ret.setName(names[type]);
        }
        return ret;
    }

    public GameObject generateObject(int x, int y, RandomGenerator rand) {
        return generate(this.x + x, this.y + y, rand, type, data);
    }
}
